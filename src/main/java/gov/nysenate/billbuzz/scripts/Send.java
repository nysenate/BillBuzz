package gov.nysenate.billbuzz.scripts;

import gov.nysenate.billbuzz.disqus.DisqusPost;
import gov.nysenate.billbuzz.model.BillBuzzApproval;
import gov.nysenate.billbuzz.model.BillBuzzAuthor;
import gov.nysenate.billbuzz.model.BillBuzzParty;
import gov.nysenate.billbuzz.model.BillBuzzSenator;
import gov.nysenate.billbuzz.model.BillBuzzSubscription;
import gov.nysenate.billbuzz.model.BillBuzzThread;
import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;
import gov.nysenate.billbuzz.util.Mailer;
import gov.nysenate.billbuzz.util.PrefixedBeanProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

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
        BillBuzzDAO dao = new BillBuzzDAO();
        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

        List<BillBuzzApproval> approvals = getApprovals(runner);
        List<BillBuzzSenator> senators = dao.getSenators(dao.getSession());

        // Senators need to be organized by party and name
        HashMap<String, BillBuzzSenator> senatorsByName = new HashMap<String, BillBuzzSenator>();
        HashMap<String, List<BillBuzzSenator>> senatorsByParty = new HashMap<String, List<BillBuzzSenator>>();
        for (BillBuzzSenator senator : senators) {
            for (BillBuzzParty party : senator.getParties()) {
                if (!senatorsByParty.containsKey(party.getId())) {
                    senatorsByParty.put(party.getId(), new ArrayList<BillBuzzSenator>());
                }
                senatorsByParty.get(party.getId()).add(senator);
            }
            senatorsByName.put(senator.getShortName(), senator);
        }

        // Organize approvals by sponsor
        TreeMap<String, Set<BillBuzzApproval>> sponsorApprovals = new TreeMap<String, Set<BillBuzzApproval>>();
        for (BillBuzzApproval approval : approvals) {
            String sponsor = approval.getThread().getSponsor().toLowerCase();
            if (!sponsorApprovals.containsKey(sponsor)) {
                sponsorApprovals.put(sponsor, new TreeSet<BillBuzzApproval>());
            }
            sponsorApprovals.get(sponsor).add(approval);
        }


        for (BillBuzzUser user : getUsers(runner)) {

            // For every user, get a list of subscribed sponsors.
            Set<BillBuzzSenator> userSubscriptions = new TreeSet<BillBuzzSenator>();
            for (BillBuzzSubscription subscription : user.getSubscriptions()) {
                if (subscription.getCategory().equals("party")) {
                    userSubscriptions.addAll(senatorsByParty.get(subscription.getValue()));
                }
                else {
                    userSubscriptions.add(senatorsByName.get(subscription.getValue()));
                }
            }

            // Get a list of approved comments on bills sponsored by these people.
            Map<BillBuzzSenator, Map<BillBuzzThread, Set<BillBuzzApproval>>> userApprovals = new TreeMap<BillBuzzSenator, Map<BillBuzzThread, Set<BillBuzzApproval>>>();
            for (BillBuzzSenator senator : userSubscriptions) {
                if (sponsorApprovals.containsKey(senator.getShortName())) {
                    Map<BillBuzzThread, Set<BillBuzzApproval>> senatorApprovals = new TreeMap<BillBuzzThread, Set<BillBuzzApproval>>();
                    for (BillBuzzApproval approval : sponsorApprovals.get(senator.getShortName())) {
                        // Shorten the thread title for the email
                        String title = approval.getThread().getTitle().replace("NY Senate Open Legislation - ", "");
                        if (title.length() > 100) {
                            title = title.subSequence(0, 100)+"...";
                        }
                        approval.getThread().setTitle(title);

                        Set<BillBuzzApproval> threadApprovals = senatorApprovals.get(approval.getThread());
                        if (threadApprovals == null) {
                            threadApprovals = new TreeSet<BillBuzzApproval>();
                            senatorApprovals.put(approval.getThread(), threadApprovals);
                        }
                        threadApprovals.add(approval);
                    }
                    userApprovals.put(senator, senatorApprovals);
                }
            }

            // Send out a mailing to that user.
            logger.info("Sending update to "+user.getEmail());
            VelocityContext context = new VelocityContext();
            context.put("user", user);
            context.put("dateFormat", new SimpleDateFormat("MMMM dd yyyy 'at' hh:mm a"));
            context.put("userApprovals", userApprovals);
            Mailer.send("billbuzz_digest", user, context);
        }
    }

    private List<BillBuzzUser> getUsers(QueryRunner runner) throws SQLException
    {
        return runner.query(
                "SELECT billbuzz_user.id as userId," +
                "       billbuzz_user.email as userEmail," +
                "       billbuzz_user.firstName as userFirstName," +
                "       billbuzz_user.lastName as userLastName," +
                "       billbuzz_user.activated as userActivated," +
                "       billbuzz_user.confirmedAt as userConfirmedAt," +
                "       billbuzz_user.createdAt as userCreatedAt," +
                "       billbuzz_subscription.id as subscriptionId," +
                "       billbuzz_subscription.userId as subscriptionUserId," +
                "       billbuzz_subscription.category as subscriptionCategory," +
                "       billbuzz_subscription.value as subscriptionValue," +
                "       billbuzz_subscription.createdAt as subscriptionCreatedAt " +
                "FROM billbuzz_user, billbuzz_subscription " +
                "WHERE billbuzz_user.id = billbuzz_subscription.userId" +
                "  AND billbuzz_user.activated = 1 " +
                "ORDER BY userId",
                new ResultSetHandler<List<BillBuzzUser>>() {
                    public List<BillBuzzUser> handle(ResultSet rs) throws SQLException
                    {
                        BillBuzzUser currentUser = null;
                        List<BillBuzzUser> users = new ArrayList<BillBuzzUser>();
                        PrefixedBeanProcessor beanProcessor = new PrefixedBeanProcessor();
                        while(rs.next()) {
                            BillBuzzUser user = beanProcessor.toBean(rs, BillBuzzUser.class, "user");
                            if (currentUser == null) {
                                currentUser = user;
                            }
                            else if (!currentUser.equals(user)) {
                                users.add(currentUser);
                                currentUser = user;
                            }
                            BillBuzzSubscription subscription = beanProcessor.toBean(rs, BillBuzzSubscription.class, "subscription");
                            currentUser.getSubscriptions().add(subscription);
                        }
                        users.add(currentUser);
                        return users;
                    }
                }
        );
    }

    private List<BillBuzzApproval> getApprovals(QueryRunner runner) throws SQLException
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
            "FROM billbuzz_approval, billbuzz_thread, billbuzz_author, billbuzz_post, billbuzz_update " +
            "WHERE billbuzz_approval.updateId=billbuzz_update.id" +
            "  AND billbuzz_update.sentAt IS NULL" +
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
            }
        );
    }
}
