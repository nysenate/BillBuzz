package gov.nysenate.billbuzz.script;

import gov.nysenate.billbuzz.model.persist.Senator;
import gov.nysenate.billbuzz.model.persist.User;
import gov.nysenate.billbuzz.model.persist.UserAuth;
import gov.nysenate.billbuzz.service.PMF;
import gov.nysenate.util.Config;
import gov.nysenate.util.DB;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 * This script dumps users and subscriptions from the old data schema to new one. The old schema
 * only tracked sponsor subscriptions so a heuristic is used to determine which parties a user may
 * have subscribed to (instead of a collection of individual senators).
 *
 * For users that have not yet authenticated, their authentication key is preserved as a new
 * billbuzz_confirmation record and and authenticate.jsp requests will properly redirect to the
 * new sign-up confirmation page.
 *
 * @author GraylinKim
 *
 */
public class Dump {

	public static void main(String[] args) throws IOException, ConfigurationException, SQLException {
	    Date now = new Date();
        Config config = new Config("app.config");
        QueryRunner runner = new QueryRunner(new DB(config, "db").getDataSource());

        // Get all the senators and collate them by name and party for reference below
		@SuppressWarnings("unchecked")
	    Collection<Senator> senators = (Collection<Senator>)PMF.getDetachedObjects(Senator.class);
		Map<String, Senator> senatorsByName = new HashMap<String, Senator>();
		for(Senator senator : senators) {
		    senatorsByName.put(senator.getOpenLegName(), senator);
		}

		@SuppressWarnings("unchecked")
		Collection<User> users = (Collection<User>) PMF.getDetachedObjects(User.class);
		for(User user:users) {
		    // Each user can have zero or more subscriptions in the categories below
            Map<String, Set<String>> userSubscriptions = new HashMap<String, Set<String>>();
            userSubscriptions.put("all", new TreeSet<String>());
            userSubscriptions.put("other", new TreeSet<String>());
            userSubscriptions.put("sponsor", new TreeSet<String>());

            // Check other data flag
            if (user.getOtherData()) {
                userSubscriptions.get("other").add("other");
            }

            // Check to see if they are subscribed to all updates
			for(String subscription : user.getSubscriptions()) {
			    if (subscription.equals("all")) {
			        userSubscriptions.get("all").add("all");
			        break;
			    }
			    else if (senatorsByName.containsKey(subscription)) {
			        userSubscriptions.get("sponsor").add(subscription);
			    }
			    else {
			        System.out.println("Unknown subscription value: "+subscription);
			    }
			}

			// Insert Statements
			boolean isAuth = user.getAuth().equals("y");
			runner.update("INSERT INTO billbuzz_user (email, firstname, lastName, activated, confirmedAt, createdAt) VALUES (?, ?, ?, ?, ?, ?)", user.getEmail(), user.getFirstName(), user.getLastName(), isAuth, now, now);
			BigInteger userId = runner.query("SELECT last_insert_id();", new ScalarHandler<BigInteger>());
	        if (!isAuth) {
	            UserAuth auth = (UserAuth) PMF.getDetachedObject(UserAuth.class, "email", user.getEmail());
	            runner.update("INSERT INTO billbuzz_confirmation (code, action, userId, createdAt) VALUES (?, ?, ?, ?)", auth.getHash(), "signup", userId, now);
	        }
			for (String category : userSubscriptions.keySet()) {
			    for (String value : userSubscriptions.get(category)) {
			        runner.update("INSERT INTO billbuzz_subscription (userId, category, value, createdAt) VALUES (?, ?, ?, ?)", userId, category, value, now);
			    }
			}
		}
	}
}