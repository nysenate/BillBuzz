package gov.nysenate.billbuzz.scripts;

import gov.nysenate.billbuzz.disqus.DisqusPost;
import gov.nysenate.billbuzz.model.BillBuzzApproval;
import gov.nysenate.billbuzz.model.BillBuzzAuthor;
import gov.nysenate.billbuzz.model.BillBuzzThread;
import gov.nysenate.billbuzz.model.BillBuzzUpdate;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.PrefixedBeanProcessor;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class Send extends BaseScript
{
    private static final Logger logger = Logger.getLogger(Send.class);

    public static Pattern billPattern = Pattern.compile("/bill/([A-Z][0-9]+[A-Z]?)(?:-([0-9]+))?", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws Exception
    {
        new Send().run(args);
    }

    public void execute(CommandLine opts) throws Exception
    {
        List<BillBuzzApproval> approvals = new ArrayList<BillBuzzApproval>();
        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
        for (BillBuzzUpdate update : runner.query("SELECT * FROM billbuzz_update WHERE sentAt IS NULL", new BeanListHandler<BillBuzzUpdate>(BillBuzzUpdate.class))) {
            logger.info("Processing update #"+update.getId()+" created at: "+update.getCreatedAt());
            approvals.addAll(getApprovals(update.getId(), runner));
        }

        Map<String, Map<String, Set<BillBuzzApproval>>> approvalsBySponsor = new TreeMap<String, Map<String, Set<BillBuzzApproval>>>();
        for (BillBuzzApproval approval : approvals) {
            String sponsor = approval.getThread().getSponsor();
            String billId = approval.getThread().getBillId();
            if (!approvalsBySponsor.containsKey(sponsor)) {
                Map<String, Set<BillBuzzApproval>> approvalsByBill = new TreeMap<String, Set<BillBuzzApproval>>();
                approvalsBySponsor.put(sponsor, approvalsByBill);
            }
            if (!approvalsBySponsor.get(sponsor).containsKey(billId)){
                TreeSet<BillBuzzApproval> set = new TreeSet<BillBuzzApproval>(new Comparator<BillBuzzApproval>(){
                    public int compare(BillBuzzApproval a, BillBuzzApproval b) {
                        return a.getPost().getCreatedAt().compareTo(b.getPost().getCreatedAt());
                    }
                });

                set.add(approval);
                approvalsBySponsor.get(sponsor).put(billId, set);
            }
            approvalsBySponsor.get(sponsor).get(billId).add(approval);
        }

        Velocity.init();
        VelocityContext context = new VelocityContext();
        context.put("approvalsBySponsor", approvalsBySponsor);
        Template template = Velocity.getTemplate("src/main/resources/templates/billbuzz/update.html", "UTF-8");

        StringWriter out = new StringWriter();
        template.merge(context, out);
        System.out.println(out);

        // Send the email message
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.sendgrid.net");
        email.setSmtpPort(587);
        email.setAuthentication("intern.mail@nysenate.gov", "springIntern2012");
        email.addTo("kim@nysenate.gov", "Graylin Kim");
        email.setFrom("billbuzz@nysenate.gov", "BillBuzz");
        email.setSubject("Test HTML email");
        email.setHtmlMsg(out.toString());
        email.setTextMsg("Your email client does not support HTML messages");
        email.send();

        logger.info("Done.");
    }

    private List<BillBuzzApproval> getApprovals(Integer updateId, QueryRunner runner) throws SQLException
    {
        return runner.query(
            "SELECT billbuzz_approval.authorId, " +
            "       billbuzz_approval.postId," +
            "       billbuzz_approval.updateId," +
            "       billbuzz_approval.threadId," +
            "       billbuzz_author.username as authorUsername, " +
            "       billbuzz_author.name as authorName, " +
            "       billbuzz_author.url as authorUrl, " +
            "       billbuzz_author.avatarUrl as authorAvatarUrl, " +
            "       billbuzz_author.profileUrl as authorProfileUrl, " +
            "       billbuzz_author.emailHash as authorEmailHash, " +
            "       billbuzz_author.location as authorLocation, " +
            "       billbuzz_author.about as authorAbout, " +
            "       billbuzz_author.isPrimary as authorIsPrimary, " +
            "       billbuzz_author.isPrivate as authorIsPrivate, " +
            "       billbuzz_author.isAnonymous as authorIsAnonymous, " +
            "       billbuzz_author.isFollowing as authorIsFollowing, " +
            "       billbuzz_author.isFollowedBy as authorIsFollowedBy, " +
            "       billbuzz_author.rep as authorRep, " +
            "       billbuzz_author.reputation as authorReputation, " +
            "       billbuzz_author.joinedAt as authorJoinedAt, " +
            "       billbuzz_author.updatedAt as authorUpdatedAt, " +
            "       billbuzz_post.parentId as postParentId, " +
            "       billbuzz_post.juliaFlagged as postJuliaFlagged, " +
            "       billbuzz_post.isFlagged as postIsFlagged, " +
            "       billbuzz_post.isDeleted as postIsDeleted, " +
            "       billbuzz_post.isHighlighted as postIsHighlighted," +
            "       billbuzz_post.isEdited as postIsEdited, " +
            "       billbuzz_post.isApproved as postIsApproved, " +
            "       billbuzz_post.isSpam as postIsSpam, " +
            "       billbuzz_post.rawMessage as postRawMessage, " +
            "       billbuzz_post.message as postMessage, " +
            "       billbuzz_post.points as postPoints, " +
            "       billbuzz_post.likes as postLikes, " +
            "       billbuzz_post.dislikes as postDislikes, " +
            "       billbuzz_post.userScore as postUserScore, " +
            "       billbuzz_post.numReports as postNumReports, " +
            "       billbuzz_post.createdAt as postCreatedAt, " +
            "       billbuzz_post.updatedAt as postUpdatedAt, " +
            "       billbuzz_thread.authorId as threadAuthorId, " +
            "       billbuzz_thread.billid as threadBillId," +
            "       billbuzz_thread.sponsor as threadSponsor, " +
            "       billbuzz_thread.isDeleted as threadIsDeleted, " +
            "       billbuzz_thread.isClosed as threadIsClosed," +
            "       billbuzz_thread.userSubscription as threadUserSubscription, " +
            "       billbuzz_thread.link as threadLink, " +
            "       billbuzz_thread.slug as threadSlug, " +
            "       billbuzz_thread.title as threadTitle, " +
            "       billbuzz_thread.message as threadMessage, " +
            "       billbuzz_thread.feed as threadFeed, " +
            "       billbuzz_thread.category as threadCategory, " +
            "       billbuzz_thread.posts as threadPosts, " +
            "       billbuzz_thread.likes as threadLikes, " +
            "       billbuzz_thread.dislikes as threadDislikes, " +
            "       billbuzz_thread.reactions as threadReactions, " +
            "       billbuzz_thread.userScore as threadUserScore, " +
            "       billbuzz_thread.createdAt as threadCreatedAt, " +
            "       billbuzz_thread.updatedAt as threadUpdatedAt " +
            "FROM billbuzz_approval, billbuzz_thread, billbuzz_author, billbuzz_post " +
            "WHERE billbuzz_approval.updateId=?" +
            "  AND billbuzz_approval.threadId=billbuzz_thread.id" +
            "  AND billbuzz_approval.authorId=billbuzz_author.id" +
            "  AND billbuzz_approval.postId=billbuzz_post.id",
            new ResultSetHandler<List<BillBuzzApproval>>() {
                public List<BillBuzzApproval> handle(ResultSet rs) throws SQLException
                {
                    PrefixedBeanProcessor beanProcessor = new PrefixedBeanProcessor();
                    List<BillBuzzApproval> approvals = new ArrayList<BillBuzzApproval>();
                    while(rs.next()) {
                        BillBuzzApproval approval = beanProcessor.toBean(rs, BillBuzzApproval.class, "approval");
                        approval.setPost(beanProcessor.toBean(rs, DisqusPost.class, "post"));
                        approval.setAuthor(beanProcessor.toBean(rs, BillBuzzAuthor.class, "author"));
                        approval.setThread(beanProcessor.toBean(rs, BillBuzzThread.class, "thread"));
                        approvals.add(approval);
                    }
                    return approvals;
                }
            },
            updateId
        );
    }
}
