/**
 * author: you
 * data: 2018/3/21
 * version: 1.0
 */


import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


/**
 * 依据以下的递归式子进行随机表达式的产生
 * E -> E # T / T
 * T -> T . F / F
 * F -> ( E ) / id
 */
public class Expression_recursion {


    /**
     * 构造函数
     */
    public Expression_recursion()
    {

    }

    /**
     * 随机表达式生成器
     * @return 随机生成的表达式的字符串形式
     */
    public String expression_generator(double probability)
    {
        Random random = new Random();
        double random_probability = random.nextDouble();
        if(random_probability < probability)
        {
            // E -> E +/- T
            //在expression_generator中使probability-0.1是为了使避免无限递归，出现栈溢出的异常，限制递归的成熟
            return this.expression_generator(probability-0.01) + this.first_operator_generator() + this.first_term_generator(probability);
        }
        else
        {
            // E -> T
            return this.first_term_generator(probability);
        }
    }

    /**
     * T -> T . F / F
     * @return 生成的T
     */
    public String first_term_generator(double probability)
    {
        Random random = new Random();
        double random_probability = random.nextFloat();
        if(random_probability < probability)
        {
            // T -> T *// F / F
            return this.first_term_generator(probability) + this.second_operator_generator() + this.second_term_generator(probability);
        }
        else
        {
            // T -> F
            return this.second_term_generator(probability);
        }
    }

    /**
     * F -> (E) / id
     * @return
     */
    public String second_term_generator(double probability)
    {
        Random random = new Random();
        double random_probability = random.nextFloat();
        if(random_probability < probability)
        {
//            // F -> (E)
//            probability = 2.0;  //设置probability>1.0，使得F -> (E)中的E为一个表达式，而不是一个值
            return "(" + this.expression_generator(probability) + ")";
        }
        else
        {
            // F -> id
            return factor_generator();
        }
    }

    /**
     * 随机数字生成器，产生正负整数，正负浮点数
     * @return 数字的字符串形式
     */
    public String factor_generator()
    {
        int max_number = 100;
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
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
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
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                str_operand = "-" + decimalFormat.format(operand);
            }
        }
        return str_operand;
    }

    /**
     * 随机产生运算符， + or -
     * @return
     */
    public String first_operator_generator()
    {
        Random random = new Random();
        if(random.nextBoolean() == Boolean.TRUE)
        {
            return "+";
        }
        else
        {
            return "-";
        }
    }

    /**
     * 随机产生运算符， * or /
     * @return * or /
     */
    public String second_operator_generator()
    {
        Random random = new Random();
        if(random.nextBoolean() == Boolean.TRUE)
        {
            return "*";
        }
        else
        {
            return "/";
        }
    }


    public void write_expression(ArrayList<String> arrayList_expression)
    {
        File file = new File("expression.txt");// 指定要写入的文件
        try
        {
            // 获取该文件的缓冲输出流
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            // 写入信息
            for (String anArrayList_expression : arrayList_expression) {
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

    public void call(int num)
    {
        Expression_recursion expression_recursion = new Expression_recursion();
        ArrayList<String> arrayList_expression = new ArrayList<>();
        for(int i=0; i < num; i++)
        {
            String expression = expression_recursion.expression_generator(0.3);
            arrayList_expression.add(expression);
        }
        expression_recursion.write_expression(arrayList_expression);
        expression_recursion.read_expression("expression.txt");
    }

    public static void main(String[] args)
    {
        int length = 5;
        Expression_recursion expression_recursion = new Expression_recursion();
        ArrayList<String> arrayList_expression = new ArrayList<>();
        for(int i=0; i < length; i++)
        {
            String expression = expression_recursion.expression_generator(0.3);
            arrayList_expression.add(expression);
        }
        expression_recursion.write_expression(arrayList_expression);
        expression_recursion.read_expression("expression.txt");
    }
}
