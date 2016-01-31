import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Tweeter {

	static String consumerKeyStr = "";
	static String consumerSecretStr = "";
	static String accessTokenStr = "";
	static String accessTokenSecretStr = "";
	static Twitter twitter = new TwitterFactory().getInstance();

	public Tweeter() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
		AccessToken accessToken = new AccessToken(accessTokenStr, accessTokenSecretStr);
		twitter.setOAuthAccessToken(accessToken);
	}

	public void tweet(String tweet) {
		try {
			twitter.updateStatus(tweet);
		} catch (TwitterException te) {
			te.printStackTrace();
		}
	}
}