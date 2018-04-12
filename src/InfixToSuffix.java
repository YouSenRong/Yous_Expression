/**
author: You
date:   2017/10/5
功能：   实现把中缀表达式转后缀表达式，未完成所有测试，但是转化的功能基本上已经实现；
         中缀表达式的括号处理中，左括号(优先权高，压入中时为了降低优先权，采用@代替左括号(压入栈中，
         而@符号在Hashtable中的优先权比较低，并可与右括号)一起成对消掉，这样做的目的是为了提高代码统一性；
 */
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfixToSuffix {

    //待处理的表达式的中缀形式
    private String str_infix_expression;

    //待处理的表达式的中缀形式的ArrayList,数字符号版本
    private ArrayList<String> arrayList_infix_expression_character = new ArrayList<>();
    //待处理的表达式的中缀形式的ArrayList,数字版本
    private ArrayList<String> arrayList_infix_expression_number = new ArrayList<>();

    //中缀转后缀后的后缀表达式的ArrayList
    private ArrayList<String> arrayList_suffix_expression = new ArrayList<>();

    //Hashtable数据结构，用于进行运算符和优先权的映射
    private Hashtable<Character,Integer> ht = new Hashtable<>();

    //运算符数组
    private char Key[] = {'#', ')', '(', '+', '-', '*', '/', '@'};
    //运算符优先权数组
    private int Priority[] = {0, 1, 5, 3, 3, 4, 4, 1};
    //操作符列表，用于负号运算符检测
    private ArrayList<Character> arrayList_operator = new ArrayList<>(Arrays.asList('#', '(', '+', '-', '*', '/'));

    /**
     * 不带参数的构造函数
     */
    InfixToSuffix()
    {
        for(int i=0; i<Key.length; i++)
        {
            ht.put(Key[i],Priority[i]);
        }
        this.str_infix_expression = "";
    }

    /**
     * 带参数的构造函数
     * @param str_infix_expression 构造中缀表达式
     */
    public InfixToSuffix (String str_infix_expression)
    {
        for(int i=0; i<Key.length; i++)
        {
            ht.put(Key[i],Priority[i]);
        }
        this.str_infix_expression = this.space_filter(str_infix_expression);
    }

    /**
     * 过滤掉原始字符串中的空格符，制表符，换行符
     * @param str_expression_original 待处理的原始运算表达式
     * @return 去掉空格符后的结果
     */
    private String space_filter(String str_expression_original)
    {
        String str_expression_result = "";
        if (str_expression_original!=null)
        {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str_expression_original);
            str_expression_result = m.replaceAll("");
        }
        return str_expression_result;
    }

//    /**
//     * 从表达式中提取运算成分出来，如操作数，运算符
//     */
//    public ArrayList<String> extractFactor()
//    {
//        //表达式组成成分列表
//        ArrayList<String> arrayList_factor = new ArrayList<>();
//
//        //表达式字符串
//        String strExpression = "#" + this.str_infix_expression + "#";
//        //判断是否已读入数字
//        boolean ifInitial = false;
//        //判断是否是负号
//        boolean ifNegative = false;
//        //表达式中的操作数临时变量
//        String str_operand = "";
//        for (int i = 1; i < strExpression.length();)
//        {
//            //判断是否为数字
//            if (((strExpression.charAt(i) >= '0') && (strExpression.charAt(i) <= '9')) || (strExpression.charAt(i) == '.')) {
//                if (ifNegative)
//                {
//                    str_operand = str_operand.concat("-");
//                    ifNegative = false;
//                }
//                str_operand = str_operand.concat(String.valueOf(strExpression.charAt(i)));
//                ifInitial = true;
//                i++;
//            }
//            else
//            {
//                if (ifInitial)
//                {
//                    arrayList_factor.add(str_operand);    //把数字加入到列表中
////                    arrayList_factor.add("num");    //为了适应文法分析器的分析，用num代替实际数
//                    ifInitial = false;
//                    str_operand = "";
//                }
//                switch (strExpression.charAt(i))
//                {
//                    case '-':
//                        if(this.arrayList_operator.contains(strExpression.charAt(i-1)))
//                        {
//                            ifNegative = true;
//                            i++;
//                            break;
//                        }
//                    default:
//                        arrayList_factor.add(String.valueOf(strExpression.charAt(i)));
//                        i++;
//                }
//            }
//        }
//
//        System.out.println(arrayList_factor.toString());
//        arrayList_factor.remove(arrayList_factor.size()-1);
//        System.out.println(arrayList_factor.toString());
//        return arrayList_factor;
//    }

    /**
     * 从表达式中提取运算成分出来，如操作数，运算符，
     * 对于小数点后没有数字的进行报错处理
     * @return 返回提取出的表达式成分的ArrayList列表
     */
    public ArrayList<String> extractFactor()
    {
        //表达式组成成分列表数字符号版本
        ArrayList<String> arrayList_factor_character = new ArrayList<>();
        //表达式组成成分列表数字版本
        ArrayList<String> arrayList_factor_number = new ArrayList<>();

        //表达式字符串, 在表达式左右加上"#"字符是为了方便操作，减少判断
        String strExpression = "#" + this.str_infix_expression + "#";

        //判断是否是负号，如果出现负号则ifNegative的值为true
        boolean ifNegative = false;

        // 开始进行提取表达式中元素成分的循环
        for (int i = 1; i < strExpression.length();)
        {
            //判断是否为数字
            //如果是数字，采用确定性自动机的方式读入数字，组成一个操作数
            if (this.is_digit(strExpression.charAt(i)))
            {
                //表达式中记录操作数临时变量
                String str_operand = "";
                if (ifNegative)
                {
                    str_operand = str_operand.concat("-");
                    ifNegative = false;
                }
                str_operand = str_operand.concat(String.valueOf(strExpression.charAt(i)));
                i++;
                while (is_digit(strExpression.charAt(i)))
                {
                    str_operand = str_operand.concat(String.valueOf(strExpression.charAt(i)));
                    i++;
                }
                if (this.is_dot(strExpression.charAt(i)))
                {
                    str_operand = str_operand.concat(String.valueOf(strExpression.charAt(i)));
                    int int_temp = ++i;

                    while (is_digit(strExpression.charAt(i)))
                    {
                        str_operand = str_operand.concat(String.valueOf(strExpression.charAt(i)));
                        i++;
                    }
                    if(int_temp == i)
                    {
                        System.err.println("Error: 小数点后必须有数字, Position: " + (i-1));
                        return null;    //此处应该为抛出异常，使程序中断
                    }
                }
                    arrayList_factor_number.add(str_operand);
                    arrayList_factor_character.add("num");    //为了适应文法分析器的分析，用num代替实际数
            }
            else
            {
                // 不是数字则对符号进行处理，对于'-'要判断其是负号还是减号
                switch (strExpression.charAt(i))
                {
                    case '-':
                        //如果是减号
                        if(this.arrayList_operator.contains(strExpression.charAt(i-1)))
                        {
                            ifNegative = true;
                            i++;
                            break;
                        }
                    default:
                        //普通符号则直接加入
                        arrayList_factor_number.add(String.valueOf(strExpression.charAt(i)));
                        arrayList_factor_character.add(String.valueOf(strExpression.charAt(i)));
                        i++;
                }
            }
        }
        // 删掉在表达式末尾加进去的"#"
        arrayList_factor_number.remove(arrayList_factor_number.size()-1);
        arrayList_factor_character.remove(arrayList_factor_character.size()-1);
        this.arrayList_infix_expression_number = (ArrayList<String>)arrayList_factor_number.clone();
        this.arrayList_infix_expression_character = (ArrayList<String>)arrayList_factor_character.clone();

//        System.out.println(arrayList_factor.toString());
        return arrayList_factor_number;
    }

    //把中缀表达式转后缀表达式
//    public String InToSuf()
//    {
//        //操作符栈
//        Stack<Character> CharStack = new Stack<>();
//        //表达式字符串
//        String strExpression = "#" + this.str_infix_expression + "#";
//        //判断是否已读入数字
//        boolean ifInitial = false;
//        //判断是否时负号
//        boolean ifNegative = false;
//        //初始化操作
//        String strResult = "";
//        CharStack.push('#');
//
//        for (int i = 1; !CharStack.empty(); )
//        {
//            //判断是否为数字
//            if (((strExpression.charAt(i) >= '0') && (strExpression.charAt(i) <= '9')) || (strExpression.charAt(i) == '.'))
//            {
//                if(ifNegative)
//                {
//                    strResult = strResult.concat("-");
//                    ifNegative = false;
//                }
//                strResult = strResult.concat(String.valueOf(strExpression.charAt(i)));
//                ifInitial = true;
//                i++;
//            }
//            else
//            {
//                //取出栈中的元素
//                char charTemp = CharStack.peek();
//                //如果之前都数字，则在数字之后加上空格
//                if (ifInitial)
//                {
//                    strResult = strResult.concat(" ");
//                    ifInitial = false;
//                }
//                //如果括号匹配，成对的括号消除
//                if(strExpression.charAt(i) == ')' && charTemp == '@')
//                {
//                    CharStack.pop();
//                    i++;
//                }
//                else if(strExpression.charAt(i) == '-' && this.arrayList_operator.contains(strExpression.charAt(i-1)))
//                {
//                    ifNegative = true;
//                    i++;
//                }
//                //栈中的运算符的优先级大于表达式当前位置运算符优先级
//                else if (ht.get(charTemp) >= ht.get(strExpression.charAt(i)) )
//                {
//                    //运算符加入运算表达式中
//                    strResult = strResult.concat(String.valueOf(CharStack.pop()) + " ");
//                }
//                else
//                {
//                    //降低左括号(优先级,如果遇到左括号则把@压入运算符栈
//                    if(strExpression.charAt(i) == '(')
//                    {
//                        CharStack.push('@');
//                    }
//                    //普通符号则正常压入栈中
//                    else
//                    {
//                        CharStack.push(strExpression.charAt(i));
//                    }
//                    i++;
//                }
//            }
//        }
//        //去掉表达式最后的空格键和栈底标识符#
//        strResult = strResult.substring(0,strResult.length()-2);
//        //返回结果表达式
//        return strResult;
//    }


    /**
     * 把传入的存储在ArrayList的字符串中缀表达式转化为后缀表达式，存储在ArrayList中
     * @param arrayList_infix_expression 中缀表达式ArrayList
     * @return 后缀表达式ArrayList
     */
    public ArrayList<String> InToSuf(ArrayList<String> arrayList_infix_expression)
    {

        arrayList_infix_expression.add("#");

        //操作符栈
        Stack<Character> CharStack = new Stack<>();
        CharStack.push('#');

        // 中缀转后缀的结果
        ArrayList<String> arrayList_suffix_expression = new ArrayList<>();

        for (int i = 0; !CharStack.empty(); )
        {
            //判断是否为数字
            if (is_digit(arrayList_infix_expression.get(i).charAt(0)) || arrayList_infix_expression.get(i).length() > 1)
            {
                arrayList_suffix_expression.add(arrayList_infix_expression.get(i));
                i++;
            }
            else
            {
                //取出栈中的元素
                char charTemp = CharStack.peek();

                //如果括号匹配，成对的括号消除
                if(arrayList_infix_expression.get(i).charAt(0) == ')' && charTemp == '@')
                {
                    CharStack.pop();
                    i++;
                }
                //栈中的运算符的优先级大于表达式当前位置运算符优先级
                else if (ht.get(charTemp) >= ht.get(arrayList_infix_expression.get(i).charAt(0)) )
                {
                    //运算符加入运算表达式中
//                    strResult = strResult.concat(String.valueOf(CharStack.pop()) + " ");
                    arrayList_suffix_expression.add(String.valueOf(CharStack.pop()));
                }
                else
                {
                    //降低左括号(优先级,如果遇到左括号则把@压入运算符栈
                    if(arrayList_infix_expression.get(i).charAt(0) == '(')
                    {
                        CharStack.push('@');
                    }
                    //普通符号则正常压入栈中
                    else
                    {
                        CharStack.push(arrayList_infix_expression.get(i).charAt(0));
                    }
                    i++;
                }
            }
        }
        arrayList_suffix_expression.remove(arrayList_suffix_expression.size()-1);
        this.arrayList_suffix_expression = arrayList_suffix_expression;
        //返回结果表达式
        return arrayList_suffix_expression;
    }

    /**
     * 判断字符是否是数字
     * @param char_temp 待判断字符
     * @return 返回判定结果，如果是数字字符返回true, 不是数字字符返回false
     */
    private Boolean is_digit(char char_temp)
    {
        return char_temp >= '0' && char_temp <= '9';
    }

    /**
     * 判断字符是否是'.'dot字符
     * @param char_temp 待判断字符
     * @return 返回判定结果，如果是数字字符返回true，不是数字字符返回false
     */
    private Boolean is_dot(char char_temp)
    {
        return char_temp == '.';
    }

    /**
     * 设置类中的str_infix_expression成员
     * @param str_infix_expression 待处理的中缀表达式的字符串
     */
    public void setStr_infix_expression(String str_infix_expression)
    {
        this.str_infix_expression = space_filter(str_infix_expression);
    }

    public ArrayList<String> getArrayList_infix_expression_character()
    {
        return (ArrayList<String>)this.arrayList_infix_expression_character.clone();
    }

    public ArrayList<String> getArrayList_infix_expression_number() {
        return (ArrayList<String>)arrayList_infix_expression_number.clone();
    }

    public ArrayList<String> getArrayList_suffix_expression()
    {
        return (ArrayList<String>)this.arrayList_suffix_expression.clone();
    }

    /**
     * 输出后缀表达式
     */
    public void outputArrayList_suffix_expression()
    {
        for (String anArrayList_suffix_expression : this.arrayList_suffix_expression)
        {
            System.out.print(anArrayList_suffix_expression + " ");
        }
        System.out.println();
    }

    /**
     * 通过命令行窗口输入表达式
     * @return 布尔值，输入是否正确进行
     */
    public boolean Input()
    {
        Scanner reader = new Scanner(System.in);
        System.out.print("请输入：");
        this.str_infix_expression = this.space_filter(reader.nextLine());
        return true;
    }

    public static void main(String args[])
    {
        InfixToSuffix ITS = new InfixToSuffix();
        ITS.Input();
        System.out.println(ITS.str_infix_expression);
        ITS.extractFactor();
        ArrayList<String> s = ITS.InToSuf(ITS.getArrayList_infix_expression_number());
        ITS.outputArrayList_suffix_expression();
    }

}
