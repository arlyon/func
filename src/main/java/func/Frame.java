package func;

import func.syntax.Identifier;

import java.util.EmptyStackException;
import java.util.HashMap;

public class Frame {
    private final Frame parent;
    private final int start;
    private int tempCount;
    private final HashMap<Identifier, Integer> variables;

    private Frame(int start, Frame parent) {
        variables = new HashMap<>();
        this.start = start;
        this.tempCount = start;
        this.parent = parent;
    }

    public Frame(int start) {
        this(start, null);
    }

    public void register(Identifier variable) {
        variables.put(variable, tempCount++);
    }

    public boolean exists(Identifier variable) {
        return variables.containsKey(variable);
    }

    public int find(Identifier variable) {
        Integer v = variables.get(variable);
        if (v != null) return v;
        if (parent != null) return parent.find(variable);
        return -1;
    }

    public Frame push() {
        return new Frame(tempCount, this);
    }

    public Frame pop() throws EmptyStackException {
        if (this.parent == null) throw new EmptyStackException();
        return this.parent;
    }
}
