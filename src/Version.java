public class Version {
    static double version = 0.01;
    public static String about() {
        return "Created by Eric Ye, Yaning Tan and Isfandyar Virani for ICS-4U in 2013-2014.\n" +
                "(c) 2013-2014 the authors. Licensed under the GPL v3.\n" +
                "Version " + version;
    }

    public static void printAbout() {
        System.out.println(about());
    }
}
