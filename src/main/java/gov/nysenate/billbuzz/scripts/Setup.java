package gov.nysenate.billbuzz.scripts;

import gov.nysenate.billbuzz.disqus.Disqus;
import gov.nysenate.billbuzz.disqus.DisqusListResponse;
import gov.nysenate.billbuzz.disqus.DisqusPost;
import gov.nysenate.billbuzz.disqus.DisqusThread;
import gov.nysenate.billbuzz.model.BillBuzzPost;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;
import gov.nysenate.billbuzz.util.Disqus2BillBuzz;
import gov.nysenate.util.Config;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

/**
 * Bootstraps the BillBuzz database with all the Disqus threads, posts, and authors to date.
 *
 * @author GraylinKim
 *
 */
public class Setup extends BaseScript
{
    private final Logger logger = Logger.getLogger(Setup.class);

    public static void main(String[] args) throws Exception
    {
        new Setup().run(args);
    }

    public void execute(CommandLine opts) throws IOException, SQLException, InterruptedException
    {
        Config config = Application.getConfig();
        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

        BillBuzzDAO dao = new BillBuzzDAO();
        Disqus disqus = new Disqus(
            config.getValue("disqus.public_key"),
            config.getValue("disqus.private_key"),
            config.getValue("access_token")
        );

        runner.update("TRUNCATE billbuzz_thread;");
        // Do this in chunks to avoid memory issues
        DisqusListResponse<DisqusThread> threadResponse = disqus.forumsListThreads("forum=nysenateopenleg", "limit=100", "order=asc");
        while (true) {
            for(DisqusThread thread : threadResponse.getResponse()) {
                dao.saveThread(Disqus2BillBuzz.thread(thread));
            }
            if (threadResponse.getCursor().getHasNext()) {
                // Don't want to go over query limit of 1000 an hour!
                java.lang.Thread.sleep(4000);
                System.out.println("Fetching next batch...");
                threadResponse = (DisqusListResponse<DisqusThread>)disqus.getNext(threadResponse);
            }
            else {
                break;
            }
        }
        System.out.println("Done with threads.");

        runner.update("TRUNCATE billbuzz_post;");
        runner.update("TRUNCATE billbuzz_author;");
        // Do this in chunks to avoid memory issues
        DisqusListResponse<DisqusPost> postResponse = disqus.forumsListPosts("forum=nysenateopenleg", "limit=100", "order=asc", "include=unapproved", "include=approved", "include=spam", "include=deleted", "include=flagged");
        while (true) {
            for(DisqusPost post : postResponse.getResponse()) {
                BillBuzzPost bbPost = Disqus2BillBuzz.post(post);
                dao.savePost(bbPost);
                dao.saveAuthor(bbPost.getAuthor());
            }
            if (postResponse.getCursor().getHasNext()) {
                // Don't want to go over query limit of 1000 an hour!
                java.lang.Thread.sleep(4000);
                System.out.println("Fetching next batch...");
                postResponse = (DisqusListResponse<DisqusPost>)disqus.getNext(postResponse);
            }
            else {
                break;
            }
        }
        System.out.println("Done with posts.");

    }
}
