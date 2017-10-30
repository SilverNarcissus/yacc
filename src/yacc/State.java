package yacc;

import java.util.HashMap;
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

    /**
     * 从该状态出发可以到达的状态转换图
     */
    HashMap<String, State> reachMap;

    public State(int id, Set<Production> productionSet, HashMap<String, State> ranchMap) {
        this.id = id;
        this.productionSet = productionSet;
        this.reachMap = ranchMap;
    }

    @Override
    public int hashCode() {
        return id + productionSet.hashCode() + reachMap.hashCode();
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
        return id == another.id &&
                productionSet.equals(another.productionSet)
                && reachMap.equals(another.reachMap);
    }
}
