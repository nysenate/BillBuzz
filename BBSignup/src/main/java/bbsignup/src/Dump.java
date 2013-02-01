package bbsignup.src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import bbsignup.model.User;

public class Dump {


	public static void main(String[] args) throws IOException {

		if(args.length == 0) {
			System.out.println("filename must be provided");
			return;
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(args[0])));


		@SuppressWarnings("unchecked")
		Collection<User> users = (Collection<User>) PMF.getDetachedObjects(User.class);


		for(User user:users) {

			if(user.getAuth().equals("y")) {

				bw.write(user.getFirstName() + ":" + user.getEmail()+":");

				List<String> subs = user.getSubscriptions();

				for(Iterator<String> itr = subs.iterator(); itr.hasNext();) {
					bw.write(itr.next() + ((itr.hasNext()) ? ",":""));
				}

				bw.newLine();


			}

		}

		bw.close();

	}

}
