/**
 * author: you
 * data: 2018/3/20
 * version: 1.0
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 */
public class Expression
{
    private String[] operator = {"(", ")", "*", "/", "+", "-", ";"};
    private int left_parenthesis_num;
    private int operand_num;
    private String expression;
    private ArrayList<String> arrayList_operator;

    public Expression()
    {

    }

    /**
     * Expression的构造函数
     * @param operand_num 操作数数目
     */
    public Expression(int operand_num)
    {
        this.left_parenthesis_num = 0;
        this.operand_num = operand_num;
        this.expression = "";
        arrayList_operator = new ArrayList<>();
        arrayList_operator.add("+");
        arrayList_operator.add("-");
        arrayList_operator.add("*");
        arrayList_operator.add("/");
    }

    /**
     * 以 probability 的概率产生左括号"("
     * @param probability 产生左括号的概率，如以30%的概率产生左括号，则probability=30
     * @return 左括号的字符串“(”或者空值
     */
    public String left_parenthesis_generator(int probability)
    {
        Random random = new Random();
        int random_number = random.nextInt(100);
        if(random_number < probability)
        {
            this.left_parenthesis_num++; //表达式中的左括号数加1
            return "(";
        }
        return "";
    }

    /**
     * 以 probability 的概率产生右括号“)”
     * @param probability 产生右括号的概率，如以30%的概率产生右括号，则probability=30
     * @return 右括号的字符串“)”或者空值
     */
    public String right_parenthesis_generator(int probability)
    {
        Random random = new Random();
        int random_number = random.nextInt(100);
//        if(random_number < probability && this.left_parenthesis_num > 0)
        if(random_number < probability)
        {
            this.left_parenthesis_num--; //表达式中的左括号数减1
            return ")";
        }
        return "";
    }

    /**
     * 产生一个数字，包括正数和负数
     * @param max_number 产生的数的绝对值的最大值
     * @return 产生的操作数的字符串形式
     */
    public String operand_generator(int max_number)
    {
        Random random = new Random();
        String str_operand;
        // 如果为真，产生一个正数，如果为假产生一个负数
        if(random.nextBoolean() == Boolean.TRUE)
        {
            // 如果为真产生一个整数，如果为假产生一个负数
            if(random.nextBoolean() == Boolean.TRUE)
            {
                str_operand = Integer.toString(random.nextInt(max_number) + 1);
            }
            else
            {
                float number = random.nextInt(max_number) + random.nextFloat();
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                str_operand = decimalFormat.format(number);

            }
        }
        else
        {
            // 如果为真产生一个整数，如果为假产生一个负数
            if(random.nextBoolean() == Boolean.TRUE)
            {
                str_operand = "-" + Integer.toString(random.nextInt(max_number) + 1);
            }
            else
            {
                float operand = random.nextInt(max_number) + random.nextFloat();
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                str_operand = "-" + decimalFormat.format(operand);
            }
        }
        this.operand_num--;
        return str_operand;
    }

    /**
     * 随机产生一个操作符
     * @return 四则运算操作符，+，-，*，/
     */
    public String operator_generator()
    {
        String[] operator = {"+", "-", "*", "/"};
        Random random = new Random();
        int random_number = random.nextInt(4);
        return operator[random_number];
    }


    /**
     * 随机产生一个四则运算的表达式
     * @return 返回随机产生的四则运算的表达式的字符串形式
     */
    public String expression_generator()
    {
        this.expression = "";
        String str_temp_composition = "";
        StringBuilder stringBuilder_expression = new StringBuilder();

        int temp_operand_num = this.operand_num;

        for(int i = 0; i < temp_operand_num; i++)
        {
            if(arrayList_operator.contains(str_temp_composition))
            str_temp_composition = this.left_parenthesis_generator(30);
            stringBuilder_expression.append(str_temp_composition);

            str_temp_composition = this.operand_generator(100);
            stringBuilder_expression.append(str_temp_composition);

            str_temp_composition = this.right_parenthesis_generator(30);
            stringBuilder_expression.append(str_temp_composition);

            if(i < temp_operand_num-1)
            {
                Random random = new Random();
                for(int j=0; j < random.nextInt(2); j++)
                {
                    str_temp_composition = this.expression + this.operator_generator();
                    stringBuilder_expression.append(str_temp_composition);
                }

            }
        }
        this.expression = stringBuilder_expression.toString();
        return this.expression;
    }

    public void write_expression(ArrayList<String> arrayList_expression)
    {
        File file = new File("expression_error.txt");// 指定要写入的文件
        try
        {
            // 获取该文件的缓冲输出流
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            // 写入信息
            for (String anArrayList_expression : arrayList_expression)
            {
                bufferedWriter.write(anArrayList_expression);
                bufferedWriter.newLine();// 表示换行
            }
            bufferedWriter.flush();// 清空缓冲区
            bufferedWriter.close();// 关闭输出流
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<String> read_expression(String pathname)
    {
        ArrayList<String> str_expression = new ArrayList<>();
        File file = new File(pathname);// 指定要读取的文件
        try
        {
            // 获得该文件的缓冲输入流
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";// 用来保存每次读取一行的内容
            while((line = bufferedReader.readLine()) != null)
            {
                str_expression.add(line);
            }
            bufferedReader.close();// 关闭输入流
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return str_expression;
    }

    public void setOperand_num(int operand_num)
    {
        this.operand_num = operand_num;
    }

    public void call(int num)
    {
        Expression expression = new Expression(10);
        ArrayList<String> arrayList_expression = new ArrayList<>();
        for(int i=0; i < num; i++)
        {
            expression.setOperand_num(10);
            String string_expression = expression.expression_generator();
            arrayList_expression.add(string_expression);
        }
        expression.write_expression(arrayList_expression);
    }

    public static void main(String[] args)
    {
        Expression expression = new Expression(10);
        int length =5;
        ArrayList<String> arrayList_expression = new ArrayList<>();
        for(int i=0; i < length; i++)
        {
            expression.setOperand_num(10);
            String string_expression = expression.expression_generator();
            arrayList_expression.add(string_expression);
        }
        expression.write_expression(arrayList_expression);
    }

}
