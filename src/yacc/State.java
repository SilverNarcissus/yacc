package yacc;

import java.util.Set;

/**
 * 表征DFA中的一个状态
 */
class State {
    /**
     * 该状态的编号
     */
    int id;

    /**
     * 该状态具有的产生式的集合
     */
    Set<Production> productionSet;


    public State(int id, Set<Production> productionSet) {
        this.id = id;
        this.productionSet = productionSet;
    }

    @Override
    public int hashCode() {
        return productionSet.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof State)) {
            return false;
        }

        State another = (State) o;
        return productionSet.equals(another.productionSet);
    }

    @Override
    public String toString(){
        String result = String.valueOf(id) + ": \n";
        result += productionSet.toString() + "\n---------------\n";
        return result;
    }

    /**
     * 判断该状态是否与另一个状态同心
     * @param another 另一个状态
     * @return 该状态是否与另一个状态同心
     */
    public boolean hasSameCore(State another){
        if(productionSet.size() != another.productionSet.size()){
            return false;
        }

        for(Production my : productionSet){
            boolean hasSame = false;
            for(Production other : another.productionSet) {
                if(my.right.equals(other.right)&&my.left.equals(other.left)&&my.dot==other.dot){
                    hasSame = true;
                    break;
                }
            }
            if(!hasSame){
                return false;
            }
        }

        return true;
    }
}
