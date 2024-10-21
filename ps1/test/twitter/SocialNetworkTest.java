package twitter;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // guessFollowsGraph() Tests
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet tweet = new Tweet(1, "alice", "Hello world", new Date());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet tweet = new Tweet(1, "alice", "Hello @bob", new Date());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("alice"));
        assertTrue(followsGraph.get("alice").contains("bob"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Tweet tweet = new Tweet(1, "alice", "Hello @bob and @charlie", new Date());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("alice"));
        assertTrue(followsGraph.get("alice").contains("bob"));
        assertTrue(followsGraph.get("alice").contains("charlie"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweets() {
        Tweet tweet1 = new Tweet(1, "alice", "Hello @bob", new Date());
        Tweet tweet2 = new Tweet(2, "alice", "@charlie good morning", new Date());
        List<Tweet> tweets = Arrays.asList(tweet1, tweet2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("alice"));
        assertTrue(followsGraph.get("alice").contains("bob"));
        assertTrue(followsGraph.get("alice").contains("charlie"));
    }

    // influencers() Tests

    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Set<String> followers = new HashSet<>(Arrays.asList("bob", "charlie"));
        followsGraph.put("bob", new HashSet<>());
        followsGraph.put("alice", followers);
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected alice to be the top influencer", "alice", influencers.get(0));
    }

    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob", "charlie")));
        followsGraph.put("bob", new HashSet<>(Collections.singleton("charlie")));
        followsGraph.put("charlie", new HashSet<>());

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected charlie to be the top influencer", "charlie", influencers.get(0));
        assertEquals("expected alice to be the second influencer", "alice", influencers.get(1));
        assertEquals("expected bob to be the third influencer", "bob", influencers.get(2));
    }

    @Test
    public void testInfluencersTieInFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Collections.singleton("bob")));
        followsGraph.put("charlie", new HashSet<>(Collections.singleton("bob")));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected bob to be the top influencer", "bob", influencers.get(0));
    }
}
