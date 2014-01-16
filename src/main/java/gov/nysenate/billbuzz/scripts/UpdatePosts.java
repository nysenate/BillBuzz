package gov.nysenate.billbuzz.scripts;

import gov.nysenate.billbuzz.disqus.Disqus;
import gov.nysenate.billbuzz.disqus.DisqusListResponse;
import gov.nysenate.billbuzz.disqus.DisqusPost;
import gov.nysenate.billbuzz.disqus.DisqusThread;
import gov.nysenate.billbuzz.model.BillBuzzApproval;
import gov.nysenate.billbuzz.model.BillBuzzPost;
import gov.nysenate.billbuzz.model.BillBuzzUpdate;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;
import gov.nysenate.billbuzz.util.Disqus2BillBuzz;
import gov.nysenate.util.Config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

/**
 * Checks Disqus for newly created threads and posts. Any new content is saved to the BillBuzz
 * database. Any new or previously unapproved content is checked for approval. If approvals are
 * found a new billbuzz_update record is created with billbuzz_approval records for each approved
 * post.
 *
 * @author GraylinKim
 *
 */
public class UpdatePosts extends BaseScript
{
    private static final Logger logger = Logger.getLogger(UpdatePosts.class);

    public static void main(String[] args) throws Exception
    {
        new UpdatePosts().run(args);
    }

    @SuppressWarnings("unchecked")
    public void execute(CommandLine opts) throws IOException, SQLException, InterruptedException
    {
        Config config = Application.getConfig();
        BillBuzzDAO dao = new BillBuzzDAO();
        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

        String disqusForum = "forum="+config.getValue("disqus.forum");
        Disqus disqus = new Disqus(
            config.getValue("disqus.public_key"),
            config.getValue("disqus.private_key"),
            config.getValue("disqus.access_token")
        );

        // Get and save new threads. At the time of writing the since= parameter was broken as confirmed by the
        // disqus support team with no ETA on a fix. Instead, we sort by createdAt desc and stop searching when
        // we find a thread at or before the last update time.
        int threads = 0;
        Date lastThreadUpdate = runner.query("SELECT createdAt FROM billbuzz_thread ORDER BY createdAt DESC LIMIT 1", new ScalarHandler<Date>("createdAt"));
        DisqusListResponse<DisqusThread> threadResponse = disqus.forumsListThreads(disqusForum, "limit=100", "order=desc");
        processThreads: while (true) {
            for(DisqusThread thread : threadResponse.getResponse()) {
                if (lastThreadUpdate != null && thread.getCreatedAt().before(lastThreadUpdate)) {
                    break processThreads;
                }
                else {
                    threads++;
                    dao.saveThread(Disqus2BillBuzz.thread(thread));
                }
            }
            if (threadResponse.getCursor().getHasNext()) {
                // Don't want to go over query limit!
                java.lang.Thread.sleep(4000);
                threadResponse = (DisqusListResponse<DisqusThread>)disqus.getNext(threadResponse);
            }
            else {
                break processThreads;
            }
        }
        logger.info(threads+" new threads since "+lastThreadUpdate);

        // Get old posts, could take a while. I couldn't find a way to retrieve multiple posts by id
        List<String> postIds = runner.query("SELECT id FROM billbuzz_post WHERE isApproved = 0 AND isDeleted = 0 AND isSpam = 0", new ColumnListHandler<String>("id"));
        logger.info("Checking "+postIds.size()+" old posts for updates.");
        List<DisqusPost> posts = new ArrayList<DisqusPost>();
        for (String postId : postIds) {
            posts.add(disqus.postDetails("post="+postId));
        }

        // Get new posts since last time we updated; again since= is broken here. Use the sorted approach.
        Date lastPostUpdate = runner.query("SELECT createdAt FROM billbuzz_post ORDER BY createdAt DESC LIMIT 1", new ScalarHandler<Date>("createdAt"));
        DisqusListResponse<DisqusPost> postResponse = disqus.forumsListPosts(disqusForum, "limit=100", "order=desc", "include=unapproved", "include=approved", "include=spam", "include=deleted", "include=flagged");
        processPosts: while (true) {
            for(DisqusPost post : postResponse.getResponse()) {
                if (lastPostUpdate != null && post.getCreatedAt().before(lastPostUpdate) || post.getCreatedAt().equals(lastPostUpdate)) {
                    break processPosts;
                }
                else {
                    posts.add(post);
                }
            }
            if (postResponse.getCursor().getHasNext()) {
                // Don't want to go over query limit!
                java.lang.Thread.sleep(4000);
                postResponse = (DisqusListResponse<DisqusPost>)disqus.getNext(postResponse);
            }
            else {
                break processPosts;
            }
        }
        logger.info((posts.size()-postIds.size())+" new posts since "+lastPostUpdate);

        if (!posts.isEmpty()) {
            BillBuzzUpdate update = null;

            // Update the database records and record new approvals for this update.
            for (DisqusPost post : posts) {
                BillBuzzPost bbPost = Disqus2BillBuzz.post(post);
                dao.savePost(bbPost);
                dao.saveAuthor(bbPost.getAuthor());
                if (post.getIsApproved()) {
                    // Only create a new update if it has at least 1 approval
                    if (update == null) {
                        Date now = new Date();
                        logger.info("Creating new update for: "+now);
                        update = new BillBuzzUpdate();
                        update.setCreatedAt(now);
                        dao.saveUpdate(update);
                    }
                    logger.info("New Approval: "+post.getId()+" - "+post.getCreatedAt());
                    BillBuzzApproval approval = new BillBuzzApproval(update.getId(), bbPost.getId(), bbPost.getAuthorId(), bbPost.getThreadId());
                    dao.saveApproval(approval);
                }
            }
        }
        logger.info("Done updating posts.");
    }
}
