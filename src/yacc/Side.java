package yacc;

/**
 * Created by SilverNarcissus on 2017/10/31.
 */
class Side {
    /**
     * 表中的行号
     */
    int row;

    /**
     * 表中的列号
     */
    int col;

    /**
     * 表中的状态转换码
     */
    char transition;

    /**
     * 表中的到达状态
     */
    int to;

    public Side(int row, int col, char transition, int to) {
        this.row = row;
        this.col = col;
        this.transition = transition;
        this.to = to;
    }

    @Override
    public int hashCode(){
        return row + col;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (!(o instanceof Side)) {
            return false;
        }

        Side another = (Side) o;
        return row == another.row
                && col == another.col;
    }
}
