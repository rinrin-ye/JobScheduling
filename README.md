# Job Scheduling

------



## 1. 작업 스케줄링이란?

- 작업 스케줄링(Job Scheduling) 문제란 n개의 작업, 각 작업의 수행 시간 ti(i=1, 2, 3, ... n) 그리고 m개의 동일한 기계가 주어질 떄, 모든 작업이 가장 빨리 종료되도록 작업을 기계에 배정하는 문제이다. *(단, 한 작업은 배정된 기계에서 연속적으로 수행되어야 하며 기계는 1번에 하나의 작업만을 수행한다.)*



## 2. Brute force를 이용한 최적해

우선 전수 조사를 이용해 작업 스케줄링의 최적해를 구하는 코드를 만들어 보았다. 

*<u>(이때, 기계의 개수를 2개라 가정하고 코드를 작성하였다.)</u>*

------

**BruteForce(int[] t)**

- 입력 : 각 작업의 수행 시간 행렬 t  *(기계의 수를 2개라 가정하였으므로 BruteForce(int[] t) 메소드는 각 작업의 수행 시간 행렬인 t만 인수로 받음)*
- 출력 : 작업 종료 시간 *(작업 스케줄링에서 모든 작업이 가장 빨리 종료되는 경우의 종료 시간 = 최적해)*

------

기계가 2개라고 가정한 경우이기 때문에 작업을 기계에 할당하는 경우를 2진수로 표현하여 사용하였다. 0일 경우에는 기계1, 1일 경우에는 기계2에 작업을 할당하였다. 이때, 모든 작업은 기계 2개 중 하나의 기계에 할당되므로 모든 경우의 수는 2^n(n은 작업의 개수)이다. 즉, 0부터 2^n-1을 2진수로 변환하여 각각을 하나의 경우로 사용하였다.

예를 들어 설명하자면 작업이 2개, 기계가 2개인 경우 기계를 각각 A, B라고 칭한다면 작업 2개를 2개의 기계에 할당하는 경우는 AA, AB, BA, BB로 모두 4(2^2)가지이다. 이를 0부터 3까지 2진수 00, 01, 10, 11로 변환하여 코드에 사용한 것이다.

```java
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
} //10진수를 2진수로 변환

public static int BruteForce(int[] t) {
    int n = (int) Math.pow(2, t.length); //2개의 기계에 작업을 할당하는 경우의 수
    int[] ch; //작업 할당 경우 저장 변수
    int time = 100000;
    for (int i = 0; i < n; i++) {
        ch = toB(t.length, i);
        int[] a = new int[2];
        for (int j = 0; j < ch.length; j++) {
            if (ch[j] == 0) {
                a[0] += t[j]; //0인 경우 기계1에 작업 할당
            } else {
                a[1] += t[j]; //1인 경우 기계2에 작업 할당
            }
        }
        if (time > Math.max(a[0], a[1])) {
            time = Math.max(a[0], a[1]); //a[0], a[1] 중 큰 수가 모든 작업이 종료되는 시간이고 작업 할당 경우 중 모든 작업이 종료되는 시간이 가장 빠른 것을 time에 저장
        }
    }
    return time; //가장 빠른 모든 작업 종료 시간(=최적해) return
}
```





## 3. Greedy Algorithm을 이용한 근사해

위와 같은 방법으로 전수 조사를 해 작업 스케줄링을 최적해를 구할 수는 있지만 작업 개수가 증가할수록 최적해를 구하는데 걸리는 시간은 크게 증가한다. 그래서 우리는 최적해를 찾는 것을 포기하고 최적해에 아주 가까운 해를 찾아주는 **근사 알고리즘** (Approximation Algorithm) 을 이용해 문제를 해결해보려고 한다. 이러한 근사 알고리즘은 근사해를 찾는 대신에 다항식 시간의 복잡도를 가진다.

이러한 근사 알고리즘을 이용하여 작업을 어느 기계에 배정하여야 모든 작업이 가장 빨리 종료되는지 알 수 있는 가장 간단한 방법은 그리디 방법으로 작업을 배정하는 것이다. 즉, 현재까지 배정된 작업에 대해서 가장 빨리 끝나는 기계에 새 작업을 배정하는 것이다.

------

**Approx_JobScheduling(int[] t, int m)**

- 입력 : 각 작업의 수행 시간 행렬 t, 기계의 개수 m
- 출력 : 작업 종료 시간

------



```java
public static int Approx_JobScheduling(int[] t, int m) {
    int[] L = new int[m]; //행렬 L에 각 기계에 배정된 마지막 작업 종료 시간 저장
    for (int j = 0; j < m; j++) {
        L[j] = 0; //작업 배정 전이므로 행렬 L을 0으로 초기화
    }
    for (int i = 0; i < t.length; i++) {
        int min = 0;
        for (int j = 1; j < m; j++) {
            if (L[j] < L[min])
                min = j;
        } //행렬 L을 비교하여 가장 빨리 끝나는 기계를 찾음
        L[min] = L[min] + t[i]; //가장 빨리 끝나는 기계에 새 작업 배정
    }
    int max = 0;
    for (int j = 1; j < m; j++) {
        if (L[j] > L[max])
            max = j;
    } //모든 작업 할당 후 가장 늦은 작업 종료 시간 찾기
    return L[max]; //가장 늦은 작업 종료 시간 = 모든 작업이 종료된 시간
}
```





## 4. 시간 복잡도와 근사 비율

#### 시간 복잡도

- **Brute force** : 작업의 개수가 n이고 기계의 개수가 m일 때 작업이 기계에 할당되는 모든 경우의 작업 종료 시간을 계산 -> **O(m^n)**
- **Approx_JobScheduling** : n개의 작업을 하나씩 가장 빨리 끝나는 기계에 배정하기 위해 기계를 찾는데 for-루프가 m-1번 수행되고 모든 기계의 마지막 작업 종료 시간을 살펴보아야 하므로 O(m) 시간이 걸림 -> n x O(m) + O(m) = **O(nm)**

위와 같이 근사 알고리즘은 근사해를 찾는 대신에 다항식 시간의 복잡도를 가진다.



#### 근사 비율

근사 알고리즘은 근사해가 얼마나 최적해에 근사한지를 나타내는 근사 비율(Approximation Ratio)을 알고리즘과 함께 제시하여야 한다. 근사 비율은 근사해의 값과 최적해의 값의 비율로서, 1.0에 가까울수록 정확도가 높은 알고리즘이다. 

- Approx_JobScheduling 알고리즘의 근사해를 OPT'라 하고, 최적해를 OPT라고 할 때, **OPT' <= 2OPT** 이다. 즉, 근사해는 최적해의 2배를 넘지 않는다.

* 왜 OPT' <= 2OPT 성립하는가?

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbGexsx%2FbtqETLcS6iH%2F84M44TWKitfervBxWXkoy1%2Fimg.png" alt="근사 비율 설명" width="450" height="200" />

1. 위의 그림은 Approx_JobScheduling 알고리즘을 이용하여 작업을 배정했을 때, 가장 마지막으로 배정된 작업 i가 T부터 수행되며, 모든 작업이 T+ti에 종료된 것을 보여주고 있다. 즉, <u>OPT'=T+ti </u>이다. *(ti는 작업 i의 수행시간)*

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbyzs00%2FbtqET7fwr5M%2FSUR4baQvtANnLeluklcfH0%2Fimg.png"  width="450" height="200" />

2. 위 그림에서 T'은 작업 i를 제외한 모든 작업의 수행 시간의 합을 기계의 수 m으로 나눈 것이다. 즉, T'는 작업 i를 제외한 평균 작업 종료 시간이다.
3. T <= T'이 된다. 왜냐하면 작업 i가 배정된 기계를 제외한 모든 기계에 배정된 작업은 적어도 T 이후에 종료되기 때문이다. (작업 i는 가장 빨리 끝나는 기계에 할당되므로)

<img src="https://user-images.githubusercontent.com/80517298/118993544-8fa8dd00-b9c0-11eb-9fb3-553c1e056933.jpg" alt="작업 스케줄링-근사 비율" width="450" height="300" />

4. OPT' <= 2OPT 증명
   - 부등식 1 : 위의 그림을 통해 살펴본 T <= T' 이용
   - 부등식 2 : 최적해 OPT는 모든 작업의 수행 시간의 합을 기계의 수로 나눈 값인 평균 종료 시간보다 같거나 크고 또 하나의 작업 수행 시간과 같거나 크다는 것을 부등식에 반영한 것



## 5. 자바 코드

```java
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
```



## 6. 코드 결과

- 작업의 개수 : 4, 기계의 개수 : 2 + 근사 비율 : 12/9 = 1.333...

<img src="https://user-images.githubusercontent.com/80517298/118986546-6d13c580-b9ba-11eb-87f7-dee1b1f1ae0e.jpg" alt="작업 스케줄링-1" width="450" height="250" />

- 작업의 개수 : 8, 기계의 개수 : 2 + 근사 비율 : 24/20 = 1.2

<img src="https://user-images.githubusercontent.com/80517298/118986554-6e44f280-b9ba-11eb-8d2e-319a76c56870.jpg" alt="작업 스케줄링-2" width="450" height="250" />

- 작업의 개수 : 16, 기계의 개수 : 2 + 근사 비율 = 42/41 = 1.02439024

<img src="https://user-images.githubusercontent.com/80517298/118986556-6edd8900-b9ba-11eb-978a-38cf53ede1c3.jpg" alt="작업 스케줄링-3" width="450" height="250" />

- 작업의 개수 : 8, 기계의 개수 : 2 + 근사 비율 : 23/23 = 1

<img src="https://user-images.githubusercontent.com/80517298/118986553-6e44f280-b9ba-11eb-9103-c91146eaf263.jpg" alt="작업 스케줄링-4" width="450" height="250" />

-> 위와 같이 최적해와 근사해가 동일한 경우도 있었음.



> **모두 근사해는 최적해의 2배를 넘지 않는다. **

