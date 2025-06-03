package src.Optim.RegAlloc;

import java.util.*;

import src.utils.Entity.*;

public class RIG {

    public static class RIGNode implements Comparable<RIGNode> {
        public String name;
        public HashMap<String, RIGNode> n;
        public int color;
        public int label;
        public boolean vis = false;
        public boolean colored = false;

        public RIGNode(String name) {
            this.name = name;
            n = new HashMap<>();
            color = label = 0;
        }

        @Override
        public int compareTo(RIGNode o) {
            return Integer.compare(label, o.label);
        }

        public int getColor() {
            return color;
        }
    }


    public HashMap<String, RIGNode> reg = new HashMap<>();
    public HashMap<String, RIGNode> spill = new HashMap<>();
    public HashSet<Integer> colors = new HashSet<>();
    public HashSet<Integer> stackColors = new HashSet<>();
    public ArrayList<RIGNode> regPEO = new ArrayList<>();
    public ArrayList<RIGNode> stackPEO = new ArrayList<>();

    public void add(HashMap<String, Double> m) {
        for (Map.Entry<String, Double> e : m.entrySet()) {
            reg.put(e.getKey(), new RIGNode(e.getKey()));
        }
    }

    public void addEdge(String s, String t) {
        reg.get(s).n.put(t, reg.get(t));
        reg.get(t).n.put(s, reg.get(s));
    }

    public int getN(String s) {
        return reg.get(s).n.size();
    }

    public void spillVertex(String s) {
        RIGNode u = reg.get(s);
//        for (Map.Entry<String, RIGNode> e : u.n.entrySet()) {
//            e.getValue().n.remove(s);
//        }
        spill.put(s, u);
        reg.remove(s);
    }

    public void subgraph() {
        for (Map.Entry<String, RIGNode> e : reg.entrySet()) {
            for (Map.Entry<String, RIGNode> f : spill.entrySet()) {
                e.getValue().n.remove(f.getKey());
            }
        }
        for (Map.Entry<String, RIGNode> e : spill.entrySet()) {
            for (Map.Entry<String, RIGNode> f : reg.entrySet()) {
                e.getValue().n.remove(f.getKey());
            }
        }
    }

    public void MCS(boolean isStack) {
        HashMap<String, RIGNode> g = isStack ? spill : reg;
        int cnt = g.size(), max = 0;
        HashMap<Integer, HashSet<RIGNode>> bucket = new HashMap<>();
        bucket.put(0, new HashSet<>(g.values()));
        for (int i = cnt - 1; i >= 0; --i) {
            while (!bucket.containsKey(max) || bucket.get(max).isEmpty()) {
                bucket.remove(max);
                --max;
            }
            Iterator<RIGNode> it = bucket.get(max).iterator();
            RIGNode u = it.next();
            u.label = i;
            u.vis = true;
            bucket.get(max).remove(u);
            for (Map.Entry<String, RIGNode> e : u.n.entrySet()) {
                RIGNode v = e.getValue();
                if (v.vis) continue;
                bucket.get(v.label).remove(v);
                ++v.label;
                if (!bucket.containsKey(v.label)) {
                    bucket.put(v.label, new HashSet<>());
                }
                bucket.get(v.label).add(v);
                max = Math.max(max, v.label);
            }
        }
        ArrayList<RIGNode> peo = new ArrayList<>(g.values());
        Collections.sort(peo);
        boolean isChordal = true;
        for (int i = 0; i < cnt && isChordal; ++i) {
            RIGNode r = peo.get(i);
            PriorityQueue<RIGNode> A = new PriorityQueue<>();
            for (int j = i + 1; j < cnt; ++j) {
                RIGNode ne = peo.get(j);
                if (r.n.containsKey(ne.name)) {
                    A.offer(ne);
                }
            }
            if (!A.isEmpty()) {
                RIGNode m = A.poll();
                for (var adj : A) {
                    if (!m.n.containsKey(adj.name)) {
                        isChordal = false;
                        break;
                    }
                }
            }
        }
        if (isStack) {
            stackPEO = peo;
        } else {
            regPEO = peo;
        }
        assert isChordal;
    }

    public void color(int rn) {
        // sort by label
        for (int r = regPEO.size() - 1; r >= 0; --r) {
            var u = regPEO.get(r);
            HashSet<Integer> s = new HashSet<>();
            for (Map.Entry<String, RIGNode> e : u.n.entrySet()) {
                RIGNode v = e.getValue();
                if (v.colored) {
                    s.add(v.color);
                }
            }
            for (int i = 1; ; ++i) {
                if (!s.contains(i)) {
                    assert i <= rn;
                    u.color = i;
                    colors.add(i);
                    u.colored = true;
                    Register.markReg(u.name, i);
                    break;
                }
            }
        }

    }

    public void colorStack() {
        // sort by label
        for (int r = stackPEO.size() - 1; r >= 0; --r) {
            var u = stackPEO.get(r);
            HashSet<Integer> s = new HashSet<>();
            for (Map.Entry<String, RIGNode> e : u.n.entrySet()) {
                RIGNode v = e.getValue();
                if (v.colored) {
                    s.add(v.color);
                }
            }
            for (int i = 1; ; ++i) {
                if (!s.contains(-i)) {
                    u.color = -i;
                    stackColors.add(-i);
                    u.colored = true;
                    Register.markStack(u.name, i);
                    break;
                }
            }
        }

    }


}
