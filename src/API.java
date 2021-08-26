import util.ConsoleUtil;
import util.Constant;
import util.cache.CacheUtil;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;

import static util.ConsoleUtil.println;
import static util.logging.Log.error;

public class API {

    public static final String APP_NAME = "APIv0.1";

    private final CacheUtil cacheUtil = new CacheUtil(new File(System.getProperty("user.home"), APP_NAME)); //Set Cache directory

    private final ConsoleUtil consoleUtil; //Create console util object

    private final String[] args; //Supplied console arguments

    private int progress; //Progress to be tracked while downloading

    public API(final String[] args, final InputStream inputStream, final PrintStream printStream) {
        this.args = args;
        this.consoleUtil = new ConsoleUtil(inputStream, printStream);
    }

    protected void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consoleUtil::destroy)); //Add shutdown hook

        //Load cache from disk
        cacheUtil.loadCache();
        //Display the initial menu
        consoleUtil.createOptionsMenu(new String[]{"Download", "Options", "Exit"}, integer -> {
            switch (integer) {
                case 0:
                    download();
                    break;
                case 1:
                    options();
                case 2:
                    System.exit(0);
                    break;
                default:
                    start();
            }
        });
    }

    private void options() {
        consoleUtil.createOptionsMenu(new String[]{"Filename format", "Output directory path", "Return"}, integer -> {
            switch (integer) {
                case 0:
                    fileNameFormat();
                    break;
                case 1:
                    boolean hasCachedDir = cacheUtil.cached("dir");
                    if (hasCachedDir) {
                        boolean resp = consoleUtil.option("Delete cache?");
                        if (resp) cacheUtil.removeFromCache("dir");
                    } else System.out.println("No directory was cached");
                    break;
                case 2:
                    start();
                    break;
                default:
                    options();
            }
        });
        start();
    }

    private void fileNameFormat() {
        final String nextLine = consoleUtil.readLine("Please enter the format string: ");

        final Matcher matcher = Constant.OWN_FORMAT_PATTERN.matcher(nextLine);

        if (matcher.find()) {
            final String formattedLine = matcher.group(1);
            cacheUtil.cacheObject("format", formattedLine);
            cacheUtil.flushCache();

            println("Pattern set to: " + formattedLine);
            options();

        } else {
            error("Wrong pattern supplied");
            fileNameFormat();
        }
    }

    private void download() {
        final int argumentLength = 3;
        /*
        Maps the inner arguments to the provided arguments
        array map:
        (0) url
        (1) filename
        (2) directory
        In this example the url is mapped to String[] ags index 0, the filename String[] args 2 and finally the directory
        to 1.
        The length of this array obviously has to be the same as the argument length
         */
        final int[] argumentMap = {0, 2, 1};

        if (args.length < 3 && System.in == null) {
            error("Too little arguments supplied");
            return;
        }

        boolean passedArguments = args.length == argumentLength;
        println("Arguments passed in: " + passedArguments);

        String url = "";

        if (passedArguments) {
            url = args[argumentMap[0]];
        } else {
            url = consoleUtil.readLine("Enter url: ");
        }

        boolean hasCachedDir = cacheUtil.cached("dir");

        if (!hasCachedDir) {
            cacheUtil.cacheObject("dir", consoleUtil.readLine("Enter output directory"));
            cacheUtil.flushCache(); //Write to disk, just in case
        }

        final File output = new File(passedArguments ? args[argumentMap[2]] : cacheUtil.getString("dir"));

        boolean hasPreviousFileName = cacheUtil.cached("format"); //Allows for formatting

        String fileName = "null";

        if (hasPreviousFileName) {
            fileName = String.format(cacheUtil.getString("format"), consoleUtil.readLine("Format arguments: ").split(","));
        } else {
            fileName = passedArguments ? args[argumentMap[2]] : consoleUtil.readLine("Filename: ");
        }
        initDownload(url, output, fileName);
    }

    private void initDownload(String inputURL, File output, String fileName) {
        /*
        Download
         */
        start(); //Return to start
    }
}
