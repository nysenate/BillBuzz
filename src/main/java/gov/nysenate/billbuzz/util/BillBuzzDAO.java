package gov.nysenate.billbuzz.util;

import gov.nysenate.billbuzz.model.BillBuzzApproval;
import gov.nysenate.billbuzz.model.BillBuzzAuthor;
import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.model.BillBuzzPost;
import gov.nysenate.billbuzz.model.BillBuzzSenator;
import gov.nysenate.billbuzz.model.BillBuzzSubscription;
import gov.nysenate.billbuzz.model.BillBuzzThread;
import gov.nysenate.billbuzz.model.BillBuzzUpdate;
import gov.nysenate.billbuzz.model.BillBuzzUser;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;


public class BillBuzzDAO
{
  private final Logger logger = Logger.getLogger(BillBuzzDAO.class);
  private final QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

  public int getSessionYear()
  {
    // Get the current session year
    int year = Calendar.getInstance().get(Calendar.YEAR);
    return (year % 2 == 0) ? year - 1 : year;
  }

  public void saveApproval(BillBuzzApproval approval) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_approval (updateId, postId, threadId, authorId) VALUES (?, ?, ?, ?)", approval.getUpdateId(), approval.getPostId(), approval.getThreadId(), approval.getAuthorId());
  }

  public void saveConfirmation(BillBuzzConfirmation confirmation) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_confirmation (id, code, action, userId, createdAt, expiresAt, usedAt) VALUES (?, ?, ?, ?, ?, ?, ?)", confirmation.getId(), confirmation.getCode(), confirmation.getAction(), confirmation.getUserId(), confirmation.getCreatedAt(), confirmation.getExpiresAt(), confirmation.getUsedAt());
    if (confirmation.getId() == null) {
      confirmation.setId(this.lastInsertId(runner));
    }
  }

  public BillBuzzConfirmation getOrCreateConfirmation(String action, Long userId, boolean unused, Date createdAt) throws SQLException
  {
    String query = "SELECT * FROM billbuzz_confirmation WHERE userId=? AND action=?";
    if (unused) {
      query += " AND usedAt IS NULL";
    }
    BillBuzzConfirmation confirmation = runner.query(query, new BeanHandler<BillBuzzConfirmation>(BillBuzzConfirmation.class), userId, action);
    if (confirmation == null) {
      if (createdAt == null) {
        createdAt = new Date();
      }
      String code = UUID.randomUUID().toString();
      logger.info("Creating new "+action+" confirmation for userId "+userId+": "+code);
      confirmation = new BillBuzzConfirmation(userId, action, code, createdAt, null);
      saveConfirmation(confirmation);
    }
    else {
      logger.info("Re-using existing "+action+" confirmation: "+confirmation.getCode());
    }
    return confirmation;
  }

  public BillBuzzConfirmation loadConfirmation(String action, String code) throws SQLException
  {
    return runner.query(
      "SELECT billbuzz_user.id as userId, " +
      "    billbuzz_user.email as userEmail, " +
      "    billbuzz_user.firstName as userFirstName, " +
      "    billbuzz_user.lastName as userLastName, " +
      "    billbuzz_user.activated as userActivated, " +
      "    billbuzz_user.createdAt as userCreatedAt, " +
      "    billbuzz_user.confirmedAt as userConfirmedAt, " +
      "    billbuzz_confirmation.id as confirmationId, " +
      "    billbuzz_confirmation.code as confirmationCode, " +
      "    billbuzz_confirmation.action as confirmationAction, " +
      "    billbuzz_confirmation.userId as confirmationUserId, " +
      "    billbuzz_confirmation.createdAt as confirmationCreatedAt, " +
      "    billbuzz_confirmation.expiresAt as confirmationExpiresAt, " +
      "    billbuzz_confirmation.usedAt as confirmationUsedAt " +
      "FROM billbuzz_confirmation, billbuzz_user " +
      "WHERE userId=billbuzz_user.id " +
      " AND action = ? " +
      " AND code = ? ",
      new ResultSetHandler<BillBuzzConfirmation>()
      {
        public BillBuzzConfirmation handle(ResultSet rs) throws SQLException
        {
          PrefixedBeanProcessor processor = new PrefixedBeanProcessor();
          if (rs.next()) {
            BillBuzzUser user = processor.toBean(rs, BillBuzzUser.class, "user");
            BillBuzzConfirmation confirmation = processor.toBean(rs, BillBuzzConfirmation.class, "confirmation");
            confirmation.setUser(user);
            return confirmation;
          }
          else {
            return null;
          }
        }
    }, action, code);
  }

  public List<BillBuzzSubscription> loadSubscriptions(Long userId) throws SQLException
  {
    return runner.query("SELECT * FROM billbuzz_subscription WHERE userId = ?", new BeanListHandler<BillBuzzSubscription>(BillBuzzSubscription.class), userId);
  }

  public List<BillBuzzSenator> getSessionSenators(int sessionYear)
                 throws SQLException
  {
    return runner.query("SELECT * FROM billbuzz_senator WHERE sessionYear=? and active=1 ORDER BY shortName", new BeanListHandler<BillBuzzSenator>(BillBuzzSenator.class), sessionYear);
  }

  public List<BillBuzzSenator> getSenators() throws SQLException
  {
    return runner.query("SELECT * FROM billbuzz_senator GROUP BY shortName ORDER BY shortName asc, sessionYear desc", new BeanListHandler<BillBuzzSenator>(BillBuzzSenator.class));
  }

  /**
   * This sets the auto increment id to the object after saving.
   *
   * @param update
   * @throws SQLException
   */
  public void saveUpdate(BillBuzzUpdate update) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_update (id, createdAt, sentAt) VALUES (?, ?, ?)", update.getId(), update.getCreatedAt(), update.getSentAt());
    if ( update.getId() == null ) {
      update.setId(this.lastInsertId(runner));
    }
  }

  /**
   *
   */
  public BillBuzzUser loadUser(String email) throws SQLException
  {
    return runner.query("SELECT * FROM billbuzz_user WHERE email = ?", new BeanHandler<BillBuzzUser>(BillBuzzUser.class), email);
  }

  /**
   * This sets the auto increment id to the object after saving.
   *
   * @param update
   * @throws SQLException
   */
  public void saveUser(BillBuzzUser user) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_user (id, email, firstName, lastName, activated, createdAt, confirmedAt) VALUES (?, ?, ?, ?, ?, ?, ?)", user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.isActivated(), user.getCreatedAt(), user.getConfirmedAt());
    if (user.getId() == null) {
      user.setId(this.lastInsertId(runner));
    }
  }

  /**
   * This sets the auto increment id to the object after saving.
   *
   * @param update
   * @throws SQLException
   */
  public void saveSubscription(BillBuzzSubscription subscription) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_subscription (id, userId, category, value, createdAt) VALUES (?, ?, ?, ?, ?)", subscription.getId(), subscription.getUserId(), subscription.getCategory(), subscription.getValue(), subscription.getCreatedAt());
    if (subscription.getId() == null) {
      subscription.setId(this.lastInsertId(runner));
    }
  }

  public void replaceSubscriptions(List<BillBuzzSubscription> subscriptions, Long userId) throws SQLException
  {
    runner.update("DELETE FROM billbuzz_subscription WHERE userId = ?", userId);
    for (BillBuzzSubscription subscription : subscriptions) {
      saveSubscription(subscription);
    }
  }

  /**
   * This sets the auto increment id to the object after saving.
   *
   * @param update
   * @throws SQLException
   */
  public void saveSenator(BillBuzzSenator senator) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_senator (id, name, shortName, sessionYear) VALUES (?, ?, ?, ?)", senator.getId(), senator.getName(), senator.getShortName(), senator.getSessionYear());
    if (senator.getId() == null) {
      senator.setId(this.lastInsertId(runner));
    }
  }

  public void saveThread(BillBuzzThread thread) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_thread (id, forumId, authorId, billId, sponsor, isDeleted, isClosed, userSubscription, category, feed, link, slug, title, message, posts, likes, dislikes, reactions, userScore, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        thread.getId(), thread.getForumId(), thread.getAuthorId(), thread.getBillId(), thread.getSponsor(), thread.getIsDeleted(), thread.getIsClosed(), thread.getUserSubscription(), thread.getCategory(), thread.getFeed(), thread.getLink(), thread.getSlug(), thread.getTitle(), thread.getMessage(), thread.getPosts(), thread.getLikes(), thread.getDislikes(), 0, thread.getUserScore(), thread.getCreatedAt(), thread.getUpdatedAt());
  }

  public void saveAuthor(BillBuzzAuthor author) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_author (id, username, name, url, avatarUrl, profileUrl, emailHash, location, about, isPrimary, isPrivate, isAnonymous, isFollowing, isFollowedBy, rep, reputation, joinedAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        author.getId(), author.getUsername(), author.getName(), author.getUrl(), author.getAvatarUrl(), author.getProfileUrl(), author.getEmailHash(), author.getLocation(), author.getAbout(), author.getIsPrimary(), author.getIsPrivate(), author.getIsAnonymous(), author.getIsFollowing(), author.getIsFollowedBy(), author.getRep(), author.getReputation(), author.getJoinedAt(), author.getUpdatedAt());
  }

  public void savePost(BillBuzzPost post) throws SQLException
  {
    runner.update("REPLACE INTO billbuzz_post (id, forumId, threadId, authorId, parentId, juliaFlagged, isFlagged, isDeleted, isHighlighted, isEdited, isApproved, isSpam, rawMessage, message, points, likes, dislikes, userScore, numReports, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        post.getId(), post.getForumId(), post.getThreadId(), post.getAuthorId(), post.getParentId(), post.isJuliaFlagged(), post.getIsFlagged(), post.getIsDeleted(), post.getIsHighlighted(), post.getIsEdited(), post.getIsApproved(), post.getIsSpam(), post.getRawMessage(), post.getMessage(), post.getPoints(), post.getLikes(), post.getDislikes(), post.getUserScore(), post.getNumReports(), post.getCreatedAt(), post.getUpdatedAt());
  }

  public long lastInsertId(QueryRunner runner) throws SQLException
  {
    return runner.query("SELECT last_insert_id()", new ScalarHandler<BigInteger>()).longValue();
  }
}
