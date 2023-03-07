package veinthrough.test;

public class Temp {
    int a,b;
    volatile int v,u;
    void f() {
        int i,j;

        i=a;
        j=b;
        i=v;

        j=u;

        a=i;
        b=j;

        v=i;

        u=j;

        i=u;

        j=b;
        a=i;
    }
}
