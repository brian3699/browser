package browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * A class used to store back-end information of a simple HTML browser.

 * @author Owen Astrachan
 * @author Marcin Dobosz
 * @author Yuzhang Han
 * @author Edwin Ward
 * @author Robert C. Duvall
 * @author Young Jun
 */
public class Model {

    private List<URL> myHistory;
    private int myCurrentIndex;
    private URL myCurrentURL;
    private URL myHome;
    private URL myFavorite;
    private Map<URL, Integer> myFrequentURL;


    public Model(ArrayList history, int index, URL currentURL) {
        myHistory = history;
        myCurrentIndex = index;
        myCurrentURL = currentURL;
        myFrequentURL = new HashMap<>();

    }


    // returns five most frequently visited URLs
    public ArrayList<URL> getFiveMostFrequentURL(){
        ArrayList<URL> topFive = new ArrayList<>();
        ArrayList<Map.Entry<URL, Integer>> list = new ArrayList(myFrequentURL.entrySet());
        //sorts the map by values
        list.sort(Map.Entry.comparingByValue());
        //adds five most visited URLs to returning arraylist
        if(list.size() >= 5) {
            for (int i = list.size(); i > list.size()-5; i--) {
                topFive.add(list.get(i-1).getKey());
            }
        }else{
            for (Map.Entry<URL, Integer> urlIntegerEntry : list) {
                topFive.add(urlIntegerEntry.getKey());
            }
        }
        return topFive;
    }

    // check if there is a next URL in the history
    private boolean hasNext () {
        return myCurrentIndex < (myHistory.size() - 1);
    }

    // store the given URL in both myFrequentURL and myHistory
    protected void rememberURL(URL tmp) {
        myCurrentURL = tmp;
        if(myFrequentURL.containsKey(tmp)){
            myFrequentURL.put(tmp, myFrequentURL.get(tmp) + 1);
        } else{
            myFrequentURL.put(tmp, 1);
        }

        // if successful, remember this URL
        if (hasNext()) {
            myHistory = myHistory.subList(0, myCurrentIndex + 1);
        }
        myHistory.add(myCurrentURL);
        myCurrentIndex += 1;
    }


    // Move to previous URL in the history
    public URL getNextURL (int backOrForth) {
        try {
            myCurrentIndex += backOrForth;
            return myHistory.get(myCurrentIndex);
        }catch (IndexOutOfBoundsException e){
            myCurrentIndex -= backOrForth;
            return myHistory.get(myCurrentIndex);
        }
    }

    // Deal with a potentially incomplete URL
    public URL completeURL (String possible) {
        final String PROTOCOL_PREFIX = "http://";
        if(possible == null) return null;
        try {
            // try it as is
            return new URL(possible);
        }
        catch (MalformedURLException e) {
            try {
                // e.g., let user leave off initial protocol
                return new URL(PROTOCOL_PREFIX + possible);
            }
            catch (MalformedURLException ee) {
                try {
                    // try it as a relative link
                    // FIXME: need to generalize this :(
                    return new URL(myCurrentURL.toString() + "/" + possible);
                }
                catch (MalformedURLException eee) {
                    // FIXME: not a good way to handle an error!
                    return null;
                }
            }
        }
    }
    // sets myHome to myCurrentURL
    protected void setHomeToCurrentPage(){
        myHome = myCurrentURL;
    }
    // returns myHome
    protected URL getHome(){
        return myHome;
    }
    // returns myFavorite
    protected URL getMyFavorite(){
        return myFavorite;
    }
    // sets myFavorite to myCurrentURL
    protected void setMyFavoriteToCurrentPage(){
        myFavorite = myCurrentURL;
    }
}
