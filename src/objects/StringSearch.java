package objects;

public class StringSearch {

    /**
     * This function replace all "bad" symbols to spaces.
     *
     * @param source input string
     * @return new "good" string
     */
    public static String stringToNormalString(String source) {
        String result = source.
                toLowerCase().
                replaceAll("\\+", " ").
                replaceAll("_", " ").
                replaceAll("-", " ").
                replaceAll("\\(", " (").
                replaceAll("\\)", ") ");
        while (result.contains("  ")) {
            result = result.replaceAll("  ", " ");
        }
        return result;
    }

    /**
     * This function compare two string.
     *
     * @param base   base string (base for search)
     * @param search search string
     * @return "true" - base-string contains search-string,
     * "else" - other
     */
    public static boolean compareStrings(String base, String search) {
        for (String arg : search.split(" ")) {
            if (!base.contains(arg)) {
                return false;
            }
        }
        return true;
    }

}
