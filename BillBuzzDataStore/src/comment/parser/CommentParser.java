package comment.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import comment.model.persist.DisqusThread;
import comment.model.xml.Articles;
import comments.generated.Disqus;
import comments.generated.Post;

public class CommentParser {
	
	
	public static void main(String[] args) throws IOException {
		new CommentParser().doParsing(new File("xml/old/comments21.xml"));
			
	}
	
	
	public void doParsing(File f) {
		
		
		Articles disqus = null;
		
		try {
			disqus = parseStream(new FileReader(f));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
				
//		HashMap<Integer,List<Post>> map = new HashMap<Integer,List<Post>>();
		
		/*for(Post post:posts) {
			String url = post.getLink();
			
			if(url != null && !url.equals("")) {
				List<Post> thread = null;
				Integer threadNo = post.getThread().intValue();
				
				if(map.containsKey(threadNo)) {
					thread = map.get(threadNo);
				}
				else {
					thread = new ArrayList<Post>();
				}
								
				thread.add(post);
				
				map.put(threadNo, thread);
			}
		}*/
		
		/*for(Integer key:map.keySet()) {
			System.out.println(key + ": " + map.get(key).size());
		}*/
				
		comment.model.xml.Articles articles = null;
		try {
			
			articles = parseStream(new FileReader(f));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		List<comment.model.xml.Article> artlst = articles.getArticle();
		
		for(comment.model.xml.Article article:artlst) {
			
			String url = article.getUrl();
									
			if(url != null && !url.equals("")) {
					
				DisqusThread dt = new DisqusThread(article);
				
				if(dt.getBill() != null) {
					PMF.persistDisqusThread(dt);
				}
			}
				
		}
		
	}
	
	public Articles parseStream(Reader reader) throws Exception {
		
		String packageName="comment.model.xml";
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		return (Articles)u.unmarshal(reader);
				
	}
}
