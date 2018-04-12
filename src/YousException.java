/**
 * author: you
 * data: 2018/03/22
 */

public class YousException extends Exception
{
    public YousException(String errmsg)
    {
        System.err.println("Error: " + errmsg);
    }

    public YousException(String errmsg, int line)
    {
        System.err.println("Error(line " + line + "): " + errmsg);
    }

    public YousException(String errmsg, int line, int position)
    {
        System.err.println("Error(line " + line + ",position " + position + "): " + errmsg);
    }
}
