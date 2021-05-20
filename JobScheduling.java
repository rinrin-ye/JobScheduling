import java.lang.Math;
import java.util.*;

public class JobScheduling {
    public static int[] toB(int n, int num) {
        int[] b = new int[n];
        int i = 0;
        while (num > 0) {
            b[i++] = num % 2;
            num = num / 2;
        }
        int[] r = new int[n];
        int k = 0;
        for (int j = n - 1; j >= 0; j--) {
            r[k++] = b[j];
        }
        return r;
    }

    public static int BruteForce(int[] t) {
        int n = (int) Math.pow(2, t.length);
        int[] ch;
        int time = 100000;
        for (int i = 0; i < n; i++) {
            ch = toB(t.length, i);
            int[] a = new int[2];
            for (int j = 0; j < ch.length; j++) {
                if (ch[j] == 0) {
                    a[0] += t[j];
                } else {
                    a[1] += t[j];
                }
            }
            if (time > Math.max(a[0], a[1])) {
                time = Math.max(a[0], a[1]);
            }
        }
        return time;
    }

    public static int Approx_JobScheduling(int[] t, int m) {
        int[] L = new int[m];
        for (int j = 0; j < m; j++) {
            L[j] = 0;
        }
        for (int i = 0; i < t.length; i++) {
            int min = 0;
            for (int j = 1; j < m; j++) {
                if (L[j] < L[min])
                    min = j;
            }
            L[min] = L[min] + t[i];
        }
        int max = 0;
        for (int j = 1; j < m; j++) {
            if (L[j] > L[max])
                max = j;
        }
        return L[max];
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("작업의 개수를 입력하시오.");
        int j = sc.nextInt();
        Random r = new Random();
        int[] t = new int[j];
        for (int i = 0; i < t.length; i++) {
            t[i] = r.nextInt(9) + 1;
        }
        System.out.println("각 작업의 소요시간");
        System.out.println(Arrays.toString(t));
        int rt1 = BruteForce(t);
        int rt2 = Approx_JobScheduling(t, 2);
        System.out.println("BruteForce 작업 종료 시간 : " + rt1);
        System.out.println("Greedy Algorithm 작업 종료 시간 : " + rt2);
    }
}