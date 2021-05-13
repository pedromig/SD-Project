package utils;

/* Util class used to streamline the query conditions for the searches. */
public class Compare {

    /**
     * Compares two integers.
     * @param q Integer of the query.
     * @param d Integer of the product in the database.
     * @return true if the user did not filter according to this condition or if the values match.
     */
    public static boolean integers(Integer q, Integer d)
    {
        return (q == null) || (q.equals(d));
    }

    /**
     * Compares two strings.
     * @param q String of the query.
     * @param d String of the product in the database.
     * @return true if the user did not filter according to this condition or if the values match.
     */
    public static boolean strings(String q, String d)
    {
        return (q == null) || (q.isEmpty()) || (q.equalsIgnoreCase(d));
    }

}
