import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        try {
            YousYacc yousYacc = new YousYacc(".\\grammar\\expression_grammar.bnf");
            if (yousYacc.getisLLone()) {
//                System.out.println("This BNF is a LL1");
                YousLLoneParser yousLLoneParser = new YousLLoneParser(
                        yousYacc.getTerminalHashMap(),
                        yousYacc.getProduction(),
                        yousYacc.getProductionHeadBody(),
                        yousYacc.getFirstHashMap(),
                        yousYacc.getPredictionTable());
                int num;
                System.out.println("请输入产生正确表达式的数目：");
                try {
                    Scanner input = new Scanner(System.in);
                    num = input.nextInt();
                    Expression_recursion expression_recursion = new Expression_recursion();
                    expression_recursion.call(num);
                    expression_recursion = new Expression_recursion();
                    ArrayList<String> arrayList_expression = expression_recursion.read_expression("expression.txt");
                    for (String anArrayList_expression : arrayList_expression)
                    {
                        System.out.println(anArrayList_expression);
                        InfixToSuffix infixToSuffix = new InfixToSuffix();
                        infixToSuffix.setStr_infix_expression(anArrayList_expression);
                        infixToSuffix.extractFactor();
                        yousLLoneParser.InitExpressionArrayList(infixToSuffix.getArrayList_infix_expression_character());
                        if (yousLLoneParser.Analyze())
                        {
                            infixToSuffix.InToSuf(infixToSuffix.getArrayList_infix_expression_number());
                            infixToSuffix.outputArrayList_suffix_expression();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                System.out.println("请输入错误表达式的数目：");
                try {
                    Scanner input = new Scanner(System.in);
                    num = input.nextInt();
                    Expression expression = new Expression();
                    expression.call(num);
                    ArrayList<String> arrayList_expression = expression.read_expression("expression_error.txt");
                    for (String anArrayList_expression : arrayList_expression)
                    {
                        System.out.println(anArrayList_expression);
                        InfixToSuffix infixToSuffix = new InfixToSuffix();
                        infixToSuffix.setStr_infix_expression(anArrayList_expression);
                        infixToSuffix.extractFactor();
                        yousLLoneParser.InitExpressionArrayList(infixToSuffix.getArrayList_infix_expression_character());
                        if (yousLLoneParser.Analyze())
                        {
                            infixToSuffix.InToSuf(infixToSuffix.getArrayList_infix_expression_number());
                            infixToSuffix.outputArrayList_suffix_expression();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                //手动输入测试代码段
//                InfixToSuffix infixToSuffix = new InfixToSuffix();
//                infixToSuffix.Input();
//                infixToSuffix.extractFactor();
//                yousLLoneParser.InitExpressionArrayList(infixToSuffix.getArrayList_infix_expression_character());
//                if(yousLLoneParser.Analyze())
//                {
//                    System.out.println("yes");
//                    ArrayList str_suffix = infixToSuffix.InToSuf(infixToSuffix.getArrayList_infix_expression_number());
//                    System.out.println(str_suffix);
//                }
//                else
//                {
//                    System.out.println("no");
//                }
            }
            else
            {
                System.out.println("This BNF is not a LL1");
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
