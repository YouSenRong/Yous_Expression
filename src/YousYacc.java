/**
 * author: you
 * date: 2017/12/30
 *
 */

/**
 * 功能：判断指定路径下的BNF文法是否符合LL1文法
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class YousYacc {

    //判断是不是LL(1)文法
    private boolean isLLone;

    //从.bnf文件中获取到的BNF表达式列表
    private ArrayList<String> ProductionArrayList = new ArrayList<>();
    //非终结符集合
    private Set<String> NonTerminalSet = new HashSet<>();

    //终结符集合
    private Set<String> TerminalSet = new HashSet<>();
    //有映射的终结符号集合
    private HashMap<String, Integer> TerminalHashMap = new HashMap<>();

    //产生式列表
    private ArrayList<ArrayList<String>> Production = new ArrayList<>();
    //产生式头为key，产生式列表所在位置的下标为value，用这个可以方便的通过产生式头部查找产生式体
    private HashMap<String, Integer> ProductionHeadBody = new HashMap<>();

    //First集,key为非终结符，value为key的First集
    private HashMap<String, HashSet<String>> FirstHashMap = new HashMap<>();

    //Follow集，key为非终结符，value为key的Follow集
    private HashMap<String, HashSet<String>> FollowHashMap = new HashMap<>();

    //预测分析表
    private int[][] PredictionTable;

    /**
     * 构造函数
     */
    YousYacc()
    {
        isLLone = false;
    }


    /**
     * 构造函数，调用整个类的功能，形成预测分析表
     * @param FilePath 文法文件所在路径
     */
    YousYacc(String FilePath)
    {
        isLLone = false;
        isLLone = LLOne(FilePath);
    }

    /**
     * 判断一个文法是否LL1文法，如果是LL1文法则构造预测分析表，
     * 在构造预测分析表过程中发生矛盾则不是LL1文法，相应返回true或者false
     * @param FilePath 文法文件所在路径
     * @return 如果是LL1文法返回true，不是LL1文法返回false
     */
    public boolean LLOne(String FilePath)
    {
        if(makeProductionList(FilePath))
        {
            isLLone = makeTable();
//            for(int i=1; i<NonTerminalSet.size()+1; i++)
//            {
//                for(int j=1; j<TerminalHashMap.size()+1; j++)
//                {
//                    System.out.print("{"+PredictionTable[i][j]+"}");
//                    System.out.print("  ");
//                }
//                System.out.println();
//            }
        }
        return isLLone;
    }

    /**
     * 初步判断一个BNF文法，是否是LL1文法，是则返回true否则返回false
     * @param BNFPath BNF文法文件所在路径
     * @return 如果初步判定是LL1文法则返回true，否则返回false
     */
    private boolean makeProductionList(String BNFPath)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(BNFPath));
            String production = br.readLine();
            //构造产生式ArrayList
            while (production != null)
            {
                ProductionArrayList.add(production);
                //分割产生式，分成产生式头部和产生式体
                String s[] = production.split("::=");
                //添加产生式
                addProduction(s[0], s[1]);
                //向非终结符号集中添加一个非终结符号
                addNonTerminal(s[0]);
                //向终结符号集合中添加符号
                addProductionBodyElementSet(s[1]);
                production = br.readLine();
            }
//            System.out.println("Follow集合：");
//            Set set = FollowHashMap.entrySet();
//            Iterator iterator = set.iterator();
//            while(iterator.hasNext())
//            {
//                Map.Entry map_entry = (Map.Entry) iterator.next();
//                System.out.print(map_entry.getKey()+"{");
//                for(String s : (HashSet<String>) map_entry.getValue())
//                {
//                    System.out.print(s + ", ");
//                }
//                System.out.print("}");
//                System.out.println();
//            }
//            for (ArrayList<String> al : Production)
//            {
//                for (String prod : al)
//                {
//                    System.out.print(prod + "#");
//                }
//                System.out.println();
//            }
//            for(String item : TerminatorSet)
//            {
//                System.out.println(item);
//            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //通过集合相减获得终结符号集
        makeTerminal();
        //获得终结符号的Map，不包含空串
        makeTerminalHashMap();
        try
        {
            //获得非终结符号First集
            makeFirst();
            //获得非终结符号Follow集
            makeFollow();
        }
        catch (StackOverflowError SOE)
        {
            return false;
        }
        return true;
    }

    /**
     * 向产生式列表Production和产生式头部和产生式体映射集中插入值
     * @param head 产生式头部
     * @param body 产生式体
     */
    private void addProduction(String head, String body)
    {
        ArrayList<String> tempArrayList = new ArrayList<>();
        tempArrayList.add(head.trim());
        StringTokenizer st = new StringTokenizer(body, "|");
        while (st.hasMoreTokens())
        {
            tempArrayList.add(st.nextToken());
        }
        //向产生式列表中添加产生式
        Production.add(tempArrayList);
        //为对应的产生式的产生式头添加映射
        ProductionHeadBody.put(head.trim(), Production.size());
    }

    /**
     * 添加非终结符到非终结符号集
     * @param head 产生式头部
     */
    private void addNonTerminal(String head)
    {
        NonTerminalSet.add(head.trim());
    }

    /**
     * 添加产生式体右边符号到产生式体右边的符号集
     * @param body 产生式体
     */
    private void addProductionBodyElementSet(String body)
    {
        StringTokenizer st = new StringTokenizer(body);
        while (st.hasMoreTokens())
        {
            String tempString = st.nextToken().trim();
            if (!tempString.equals("|"))
            {
                TerminalSet.add(tempString);
//                System.out.print(tempString);
            }
        }
//        System.out.println();
    }

    /**
     * 通过集合相减计算得终结符号集
     */
    private void makeTerminal()
    {
        TerminalSet.removeAll(NonTerminalSet);
        TerminalSet.add("$");
    }

    /**
     * 获得终结符号的Map，不包含空串
     */
    private void makeTerminalHashMap()
    {
        for(String terminal : TerminalSet)
        {
            if(!terminal.equals("\"\""))
            {
                TerminalHashMap.put(terminal, TerminalHashMap.size()+1);
            }
        }
    }

    //消除左递归
    private void EliminateLeftRecursion()
    {
        ArrayList<String> tempArrayList = new ArrayList<>();
        for (ArrayList<String> al : Production)
        {
//            if(tempArrayList.contains())
            for (String prod : al)
            {
                System.out.print(prod + "#");
            }
            System.out.println();
            tempArrayList.add(al.get(0));
        }
        for(int i=0; i<Production.size(); i++)
        {
            for(int j = 0; j<i; j++)
            {

            }
            //消除直接左递归

        }
    }

    //消除直接左递归
    private void EliminateDirectedRecursion()
    {

    }

    /**
     * 获得每个非终结符号的First集合
     */
    private void makeFirst()
    {
        for(String non_terminal : NonTerminalSet)
        {
            First(non_terminal);
        }
    }

    /**
     * 获得输入的非终结符号的First集
     * @param non_terminal 非终结符号
     * @return First集的内容
     */
    private HashSet<String> First(String non_terminal)
    {
        //临时的first集合
        HashSet<String> tempHashSet = new HashSet<>();

        //获得对应非终结符号的产生式的列表，
        ArrayList<String> productionArrayList = Production.get(ProductionHeadBody.get(non_terminal)-1);
        //列表0号位置存放的是产生式头，产生式体从1号位置开始
        for(int i=1; i<productionArrayList.size(); i++)
        {
            //获得产生式体中的第一个终结符或非终结符
            StringTokenizer st = new StringTokenizer(productionArrayList.get(i));
            String string =  st.nextToken();
            //如果获得的符号是终结符，加入到临时集合中
            if(TerminalSet.contains(string))
            {
                tempHashSet.add(string);
            }
            //如果获得的符号式非终结符，递归处理
            else if (NonTerminalSet.contains(string))
            {
                HashSet<String> tempFirstHashSet = First(string);
                //如果包含空串
                if(tempFirstHashSet.contains("\"\""))
                {
                    String next_string;
                    if(st.hasMoreTokens())
                    {
                        next_string = st.nextToken();
                        tempFirstHashSet.addAll(First(next_string));
                    }
                }
                tempHashSet.addAll(tempFirstHashSet);
            }
            FirstHashMap.put(non_terminal, tempHashSet);
        }
        return tempHashSet;
    }

    /**
     * 通过已经获得的First集合，计算传入的产生式体字符串的first集
     * @param production_body 产生式体
     * @return 对应产生式体的First集
     */
    private HashSet<String> getFirstInProductionBody(String production_body)
    {
        StringTokenizer st = new StringTokenizer(production_body, " ");
        HashSet<String> tempHashSet = new HashSet<>();
        String temp_character;
        if(st.hasMoreTokens())
        {
            do {
                temp_character = st.nextToken();
                if(TerminalSet.contains(temp_character))
                {
                    tempHashSet.add(temp_character);
                    return tempHashSet;
                }
                else
                {
                    tempHashSet.addAll(FirstHashMap.get(temp_character));
                }
            }while(FirstHashMap.get(temp_character).contains("\"\"") && st.hasMoreTokens());
        }
        return tempHashSet;
    }

    /**
     * 获得每个非终结符号的Follow集合
     */
    private void makeFollow()
    {
        Boolean update = true;

        //临时follow集合
        HashSet<String> tempHashSet = new HashSet<>();
        tempHashSet.clear();
        tempHashSet.add("$");
        //给开始符号的Follow集中加入终止符号$
        FollowHashMap.put(Production.get(0).get(0), tempHashSet);
        //遍历每一个产生式
        for(int i=0; update; i=((++i)%Production.size()))
        {
            //所有产生式遍历一遍之后，置update为false
            if(i==0)
            {
                update = false;
            }
            ArrayList<String> tempProductionArrayList = Production.get(i);
            //遍历产生式的每一个产生式体
            for(int j=1; j<tempProductionArrayList.size(); j++)
            {
                String tempProductionBody = tempProductionArrayList.get(j);
                StringTokenizer st = new StringTokenizer(tempProductionBody, " ");
                //把当前分割出来的产生式体放入到ArrayList中方便定位
                ArrayList<String> characterArrayList = new ArrayList<>();
                while(st.hasMoreTokens())
                {
                    characterArrayList.add(st.nextToken());
                }
                //处理产生式体最后一个字符之外的字符
                for(int k = 0; k<characterArrayList.size()-1; k++)
                {
                    if(NonTerminalSet.contains(characterArrayList.get(k)))
                    {
                        //获得该字符的Follow集，并且填入了FollowHashSet中
                        if(get_follows(characterArrayList, k))
                        {
                            update = true;
                        }
                    }
                }

                //处理最后一个符号是非终结符的情况，将当前产生式头的Follow集加入到该非终结符的Follow集中
                String lastCharacter = characterArrayList.get(characterArrayList.size()-1);
                if(NonTerminalSet.contains(lastCharacter))
                {
                    HashSet<String> tempHeadHashSet = FollowHashMap.get(Production.get(i).get(0));
                    if(FollowHashMap.get(lastCharacter) == null)
                    {
                        FollowHashMap.put(lastCharacter, tempHeadHashSet);
                        update = true;
                    }
                    else
                    {
                        if(FollowHashMap.get(lastCharacter).addAll(tempHeadHashSet))
                        {
                            update = true;
                        }
                    }
                }

                int s = characterArrayList.size();
                while(s>=2
                        && NonTerminalSet.contains(characterArrayList.get(s-1))
                        && FirstHashMap.get(characterArrayList.get(s-1)).contains("\"\"")
                        && NonTerminalSet.contains(characterArrayList.get(s-2)))
                {
                    HashSet<String> tempHeadHashSet = FollowHashMap.get(Production.get(i).get(0));
                    if(FollowHashMap.get(characterArrayList.get(s-2)) == null)
                    {
                        FollowHashMap.put(characterArrayList.get(s-2), tempHeadHashSet);
                        update = true;
                    }
                    else
                    {
                        if(FollowHashMap.get(characterArrayList.get(s-2)).addAll(tempHeadHashSet))
                        {
                            update = true;
                        }
                    }
                    s--;
                }

            }
        }

    }

    /**
     * 获得在字符串列表中的位置为index的非终结符的Follow集合
     * @param characterArrayList 字符串列表
     * @param index 下标
     * @return
     */
    private Boolean get_follows(ArrayList<String> characterArrayList, int index)
    {
        HashSet<String> tempFollowHashSet = new HashSet<>();
        int tempIndex = index;
        Boolean loop;
        do {
            tempIndex++;
            loop = false;
            if(tempIndex >= characterArrayList.size())
            {
                System.out.println("error");
            }
            if(TerminalSet.contains(characterArrayList.get(tempIndex)))
            {
                tempFollowHashSet.add(characterArrayList.get(tempIndex));
            }
            else
            {
                tempFollowHashSet.addAll(FirstHashMap.get(characterArrayList.get(tempIndex)));
                if(FirstHashMap.get(characterArrayList.get(tempIndex)).contains("\"\""))
                {
                    loop = true;
                }
            }
        }while (loop && tempIndex<characterArrayList.size()-1);

        tempFollowHashSet.remove("\"\"");
        //如果Follow集合中没有
        if(FollowHashMap.get(characterArrayList.get(index)) == null)
        {
            FollowHashMap.put(characterArrayList.get(index), tempFollowHashSet);
            return true;
        }
        return FollowHashMap.get(characterArrayList.get(index)).addAll(tempFollowHashSet);
    }

    /**
     * 通过First集和Follow集判断文法是否符合LL1文法
     * @return
     */
    private boolean isLLoneByFirstAndFollow()
    {
        for(ArrayList<String> arrayList : Production)
        {
            if(arrayList.size()> 2)
            {
                for(int i=1; i<arrayList.size(); i++)
                {
                    HashSet<String> FirstOne = getFirstInProductionBody(arrayList.get(i));
                    for(int j=i+1; j < arrayList.size(); j++)
                    {
                        HashSet<String> FirstTwo = getFirstInProductionBody(arrayList.get(j));
                        FirstOne.retainAll(FirstTwo);
                        if(FirstOne.size() != 0)
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 根据对应文法产生预测分析表
     * @return 构造成功返回true， 构造中发生矛盾，返回false
     */
    private boolean makeTable()
    {
        PredictionTable = new int[NonTerminalSet.size()+1][TerminalHashMap.size()+1];
        for(int i=0; i<NonTerminalSet.size()+1; i++)
        {
            for(int j=0; j<TerminalHashMap.size()+1; j++)
            {
                PredictionTable[i][j] = -1;
            }
        }
        for(int i = 0; i<Production.size(); i++)
        {
            ArrayList<String> production = Production.get(i);
            for(int j=1; j<production.size(); j++)
            {
                //获得产生式体的First集
                HashSet<String> temp_first_HashSet = getFirstInProductionBody(production.get(j));
                for(String first : temp_first_HashSet)
                {
                    //TerminalHashMap 中不包含空串，故遇到空串符号的时候跳过，继续执行循环
                    if(first.equals("\"\""))
                    {
                        continue;
                    }
                    //如果填表的空格已经有值，发生了冲突，不是LL1文法
                    if(PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(first)] != -1
                            && PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(first)] != j)
                    {
                        return false;
                    }
                    PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(first)] = j;


                }
                //如果 空串 在First集中
                if(temp_first_HashSet.contains("\"\""))
                {
                    HashSet<String> temp_follow_HashSet = FollowHashMap.get(production.get(0));
                    for(String follow : temp_follow_HashSet)
                    {
                        if(PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(follow)] != -1
                                && PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(follow)] != j)
                        {
                            return false;
                        }
                        PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get(follow)] = j;
                    }
                    if(FollowHashMap.get(production.get(0)).contains("$"))
                    {
                        if(PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get("$")] != -1
                                && PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get("$")] != j)
                        {
                            return false;
                        }
                        PredictionTable[ProductionHeadBody.get(production.get(0))][TerminalHashMap.get("$")] = j;
                    }
                }

            }
        }
        return true;
    }

    public boolean getisLLone()
    {
        return isLLone;
    }

    public HashMap<String, Integer> getTerminalHashMap() {
        return TerminalHashMap;
    }

    public ArrayList<ArrayList<String>> getProduction() {
        return Production;
    }

    public HashMap<String, Integer> getProductionHeadBody() {
        return ProductionHeadBody;
    }

    public HashMap<String, HashSet<String>> getFirstHashMap() {
        return FirstHashMap;
    }

    public int[][] getPredictionTable()
    {
        return PredictionTable;
    }

}























