package cz.sionzee.bukkithelper.utils;


public class Tuples {

    public static class Unit<A> {
        A a;

        public Unit(A a) {
            this.a = a;
        }

        public A a() {
            return a;
        }
    }

    public static class Pair<A, B> extends Unit<A> {
        B b;

        public Pair(A a, B b) {
            super(a);
            this.b = b;
        }

        public B b() {
            return b;
        }
    }

    public static class Triple<A, B, C> extends Pair<A, B> {
        C c;

        public Triple(A a, B b, C c) {
            super(a, b);
            this.c = c;
        }

        public C c() {
            return c;
        }
    }

    public static class Quad<A, B, C, D> extends Triple<A, B, C> {
        D d;

        public Quad(A a, B b, C c, D d) {
            super(a, b, c);
            this.d = d;
        }

        public D d() {
            return d;
        }
    }

    public static class Five<A, B, C, D, E> extends Quad<A, B, C, D> {
        E e;

        public Five(A a, B b, C c, D d, E e) {
            super(a,b,c,d);
            this.e = e;
        }

        public E e() {
            return e;
        }
    }

    public static class Six<A, B, C, D, E, F> extends Five<A, B, C, D, E> {
        F f;

        public Six(A a, B b, C c, D d, E e, F f) {
            super(a, b, c, d, e);
            this.f = f;
        }

        public F f() {
            return f;
        }
    }

    public static class Seven<A, B, C, D, E, F, G> extends Six<A, B, C, D, E, F> {
        G g;

        public Seven(A a, B b, C c, D d, E e, F f, G g) {
            super(a, b, c, d, e, f);
            this.g = g;
        }

        public G g() {
            return g;
        }
    }
}
