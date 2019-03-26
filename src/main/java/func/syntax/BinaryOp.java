package func.syntax;

public enum BinaryOp {
    Eq, Less, LessEq, NEq;

    public String toString() {
        switch (this) {
            case Eq:
                return "eq";
            case Less:
                return "less";
            case LessEq:
                return "lessEq";
            case NEq:
                return "nEq";
        }
        return null;
    }
}
