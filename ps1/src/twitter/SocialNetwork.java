package twitter;

import java.util.*;

public class SocialNetwork {

    // Helper method to extract mentions from tweet text
    private static Set<String> extractMentions(String text) {
        Set<String> mentions = new HashSet<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.startsWith("@")) {
                String mention = word.substring(1).toLowerCase();
                mentions.add(mention);
            }
        }
        return mentions;
    }

    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Set<String> mentions = extractMentions(tweet.getText());

            // Remove self-mentions if any
            mentions.remove(author);

            // Add the author and the people they mentioned to the followsGraph
            if (!followsGraph.containsKey(author)) {
                followsGraph.put(author, new HashSet<>());
            }
            followsGraph.get(author).addAll(mentions);
        }

        return followsGraph;
    }

    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCounts = new HashMap<>();

        // Calculate the number of followers for each user
        for (Set<String> follows : followsGraph.values()) {
            for (String followedUser : follows) {
                followerCounts.put(followedUser, followerCounts.getOrDefault(followedUser, 0) + 1);
            }
        }

        // Sort users by follower count in descending order
        List<String> influencers = new ArrayList<>(followerCounts.keySet());
        influencers.sort((user1, user2) -> followerCounts.get(user2) - followerCounts.get(user1));

        return influencers;
    }
}
