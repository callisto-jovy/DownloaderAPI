/**
 * This is a little API I created for the ease of use of creating a simple downloader command line interface.
 * this API is pretty bare-bones, except for the console util to display menus, etc., the cache util and some other stuff.
 * Basically the API provides simple menus and asks for the download link, etc.
 * *** Uses JSON for the cache-util ***
 */
public class MainClass {


    /**
     * Main method, the arguments are checked
     *
     * @param args supplied arguments
     */
    public static void main(final String[] args) {
        final API api = new API(args, System.in, System.out);
        api.start();
    }


}
