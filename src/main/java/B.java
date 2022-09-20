public class B extends A {
    void s(){
        System.out.println("son");
    }

    public static void main(String[] args) {
        A a = new B();
        a.s();
    }
}
