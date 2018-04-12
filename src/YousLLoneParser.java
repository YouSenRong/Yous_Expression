/**
 * author: you
 * date: 2017/12/30
 *
 */


import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class YousLLoneParser {

    //有映射的终结符号集合
    private HashMap<String, Integer> TerminalHashMap = new HashMap<>();

    //产生式列表
    private ArrayList<ArrayList<String>> Production = new ArrayList<>();
    //产生式头为key，产生式列表所在位置的下标为value，用这个可以方便的通过产生式头部查找产生式体
    private HashMap<String, Integer> ProductionHeadBody = new HashMap<>();

    //First集,key为非终结符，value为key的First集
    private HashMap<String, HashSet<String>> FirstHashMap = new HashMap<>();

    //预测分析表
    private int[][] PredictionTable;

    //表达式列表
    private ArrayList<String> ExpressionArrayList = new ArrayList<>();

    //进行分析运算的栈
    private Stack<String> AnalyzeStack = new Stack<>();

    /**
     * 构造函数
     * @param terminalHashMap LL1文法的终结符号映射集合
     * @param production LL1文法的产生式
     * @param productionHeadBody LL1文法的产生式的头部和产生式体的一个映射
     * @param predictionTable LL1文法产生的预测分析表
     */
    YousLLoneParser(HashMap<String, Integer> terminalHashMap,
                    ArrayList<ArrayList<String>> production,
                    HashMap<String, Integer> productionHeadBody,
                    int[][] predictionTable)
    {
        this.TerminalHashMap = terminalHashMap;
        this.Production = production;
        this.ProductionHeadBody = productionHeadBody;
        this.PredictionTable = predictionTable;
    }

    /**
     * 构造函数
     * @param terminalHashMap LL1文法的终结符号映射集合
     * @param production LL1文法的产生式
     * @param productionHeadBody LL1文法的产生式的头部和产生式体的一个映射
     * @param FirstHashMap LL1文法的First集合的映射集
     * @param predictionTable LL1文法的预测分析表
     */
    YousLLoneParser(HashMap<String, Integer> terminalHashMap,
                    ArrayList<ArrayList<String>> production,
                    HashMap<String, Integer> productionHeadBody,
                    HashMap<String, HashSet<String>> FirstHashMap,
                    int[][] predictionTable)
    {
        this.TerminalHashMap = terminalHashMap;
        this.Production = production;
        this.ProductionHeadBody = productionHeadBody;
        this.FirstHashMap = FirstHashMap;
        this.PredictionTable = predictionTable;
    }

    /**
     * 初始化类成员变量 ExpressionArray
     * @param arrayList
     */
    public void InitExpressionArrayList(ArrayList<String> arrayList)
    {
        this.ExpressionArrayList = arrayList;
        //添加终结标识符
        ExpressionArrayList.add("$");
    }

    /**
     * 从文件中读取输出，进行成员变量ExpressionArrayList的构造
     * @param FilePath
     */
    public void ReadtheExpression(String FilePath)
    {
        ExpressionArrayList.clear();
        try
        {
            BufferedReader br = new BufferedReader((new FileReader(FilePath)));
            String temp_string = br.readLine();
            while(temp_string != null)
            {
                ExpressionArrayList.add(temp_string.trim());
                temp_string = br.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //添加终结标识符
        ExpressionArrayList.add("$");
    }

    /**
     * 分析ExpressionArrayList是否符合对应的LL1文法
     * @return 符合LL1文法返回true，否则返回false
     */
    public boolean Analyze()
    {
        int i = 0;
        int size = ExpressionArrayList.size();
        AnalyzeStack.push("$");
        AnalyzeStack.push(Production.get(0).get(0));
        String top_string = AnalyzeStack.peek();
        while(!top_string.equals("$"))
        {
            // current_character当前处理到的字符串的位置的字符
            String current_character = ExpressionArrayList.get(i);
//            //输出分析栈中的内容
//            System.out.println(AnalyzeStack.toString());
            try
            {
                if(top_string.equals(current_character))
                {
//                    System.out.println(top_string);
                    AnalyzeStack.pop();
                    i++;
                }
                else if(TerminalHashMap.containsKey(top_string))
                {
                    // 显示错误详情
                    this.show_error_detail_lack(i);
                    return false;
                }
                else if(TerminalHashMap.containsKey(current_character) && PredictionTable[ProductionHeadBody.get(top_string)][TerminalHashMap.get(current_character)] == -1)
                {
                    // 显示错误详情
                    this.show_error_detail_lack(i);
                    return false;
                }
                else if(TerminalHashMap.containsKey(current_character) && PredictionTable[ProductionHeadBody.get(top_string)][TerminalHashMap.get(current_character)] != -1)
                {
                    int temp_body_index = PredictionTable[ProductionHeadBody.get(top_string)][TerminalHashMap.get(current_character)];
                    //输出分析过程代码段
//                    System.out.print(Production.get(ProductionHeadBody.get(top_string)-1).get(0));
//                    System.out.print("::=");
//                    System.out.println(Production.get(ProductionHeadBody.get(top_string)-1).get(temp_body_index));
                    AnalyzeStack.pop();
                    String s[] = Production.get(ProductionHeadBody.get(top_string)-1).get(temp_body_index).trim().split(" ");
                    for(int j=s.length-1; j>=0; j--)
                    {

                        if(!s[j].equals("\"\""))
                        {
//                        System.out.println(s[j]);
                            AnalyzeStack.push(s[j]);
                        }

                    }
                }
                else //出现了终结符号集中没有的符号
                {
                    this.show_error_detail_redundance(i);
                    return false;
                }
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
                return false;
            }
            top_string = AnalyzeStack.peek();
        }
        // 如果AnalyzeStack分析栈中已空，但是表达式还没分析完，说明出现了错误
        if(i < size-1)
        {
            this.show_error_detail_redundance(i);
            return false;
        }
        return true;
    }

    /**
     * 显示错误详细信息：多余的字符的错误信息
     * @param position 错误发生位置
     */
    public void show_error_detail_redundance(int position)
    {
        String str_error_message;
        str_error_message = "Error: Three might be reundance character \'" + this.ExpressionArrayList.get(position) + "\' after position " + (position);
        System.err.println(str_error_message);
    }

    /**
     * 显示错误详细信息：缺少的字符的错误信息
     * @param position 错误发生位置
     */
    private void show_error_detail_lack(int position)
    {
        String str_error_message;
        if(TerminalHashMap.containsKey(AnalyzeStack.peek()))
        {
            str_error_message = "Error: Three might be lack of character \'" + AnalyzeStack.peek() + "\' after position " + (position);
        }
        else
        {
            str_error_message = "Error: Three might be lack of character \'" + FirstHashMap.get(AnalyzeStack.peek()) + "\' after position " + (position);
        }
        System.err.println(str_error_message);
    }

}
