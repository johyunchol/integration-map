package kr.co.kkensu.integrationmap.util;

import kr.co.kkensu.integrationmap.MapPoint;

import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;

public class LocationUtil {
    public static final String DISTANCE_KM = "DISTANCE_KM";
    public static final String DISTANCE_M = "DISTANCE_M";

    public static final double d2r = Math.PI / 180.0;

    public static double distanceM(int km) {
        double meter = km * 1000f;

        return Double.parseDouble(String.format("%.1f", meter));
    }

    public static double distanceKM(int meter) {
        double km = meter / 1000f;

        return Double.parseDouble(String.format("%.1f", km));
    }

    public static double distanceKM(double lat1, double lng1, double lat2, double lng2) {
        double dlng = (lng2 - lng1) * d2r;
        double dlat = (lat2 - lat1) * d2r;

        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlng / 2.0), 2);
        double c = 2 * Math.atan2(sqrt(a), sqrt(1 - a));
        double d = 6367 * c;

        return d;
    }

    public static double distanceM(double lat1, double lng1, double lat2, double lng2) {
        return distanceKM(lat1, lng1, lat2, lng2) * 1000;
    }

    public static int distanceSecondFoot(double lat1, double lng1, double lat2, double lng2, float distance) {
        double distance_m = distanceKM(lat1, lng1, lat2, lng2) * 1000;

        if (distance_m >= 300) {
            distance_m = distance_m + 230;
        } else if (distance_m >= 200 && distance_m < 300) {
            distance_m = distance_m + 140;
        } else if (distance_m >= 100 && distance_m < 100) {
            distance_m = distance_m + 50;
        }

        int second = (int) (distance_m / distance);
        return second;
    }

    public static int distanceSecondCar(double lat1, double lng1, double lat2, double lng2, float distance) {
        double distance_m = distanceKM(lat1, lng1, lat2, lng2) * 1000;

        if (distance_m >= 280) {
            distance_m = distance_m + 1100;
        } else if (distance_m >= 150 && distance_m < 280) {
            distance_m = distance_m + 700;
        } else {
            distance_m = distance_m + 400;
        }

        int second = (int) (distance_m / distance);
        return second;
    }

    public static int distanceSecondFoot(double lat1, double lng1, double lat2, double lng2) {
        return distanceSecondFoot(lat1, lng1, lat2, lng2, 1.1f);
    }

    public static int distanceSecondCar(double lat1, double lng1, double lat2, double lng2) {
        return distanceSecondCar(lat1, lng1, lat2, lng2, 3.0f);
    }

    public static double calcDistance(double lat1, double lng1, double lat2, double lng2, double x, double y) {
        double dlng = (lng2 - lng1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlng / 2.0), 2);

        return abs(a * (x - lat1) - y + lng1) / sqrt(a * a + 1);
    }

    public static double calcDistanceMeter(double lat1, double lng1, double lat2, double lng2, double x, double y) {
        return calcDistance(lat1, lng1, lat2, lng2, x, y) * 10;
    }

    public static MapPoint calculator(MapPoint a, MapPoint b, MapPoint c) {
        double xx = 0.0;
        double yy = 0.0;

        // Ax와 Bx 가 같을때(y에 평행)
        if (a.getLongitude() == b.getLongitude()) {
            xx = a.getLongitude();
            yy = c.getLatitude();
        }
        // Ay와 By 가 같을때(x에 평행)
        else if (a.getLatitude() == b.getLatitude()) {
            xx = c.getLongitude();
            yy = a.getLatitude();
        } else {
            // 직선의 방정식(1) y= m1x + k1
            // 점A와 B를 지나는 직선의 기울기
            double m1 = (b.getLatitude() - a.getLatitude()) / (b.getLongitude() - a.getLongitude());
            //
            double k1 = -m1 * a.getLongitude() + a.getLatitude();

            // (1)과 직교하고 화면중심을 지나는 직선의 방정식 y= m2x + k2
            // 직교하기 때문에 기울기의 곱은 -1 이됨 m1 * m2 = -1
            double m2 = -1 / m1;
            double k2 = -m2 * c.getLongitude() + c.getLatitude();

            // 두 직선의 교점을 찾는다 m1x + k1 = m2x + k2
            xx = (k2 - k1) / (m1 - m2);
            yy = m1 * xx + k1;
        }

        return new MapPoint(yy, xx);
    }

    /*main() {
        double opointx, opointy, r;
        double pointx1, pointx2, pointy1, pointy2;
        double m, a, b, c, d;

        printf("\n한 원의 원점을 입력하시오 (예) 1,-2 : ");
        scanf("%lf,%lf", & opointx,&opointy);

        printf("반지름을 입력하시오 : ");
        scanf("%lf", & r);

        printf("점 1을 입력하시오 (예) 1,1 : ");
        scanf("%lf,%lf", & pointx1,&pointy1);

        fflush(stdin);

        printf("점 2를 입력하시오 (예) 1,2 : ");
        scanf("%lf,%lf", & pointx2,&pointy2);

        printf("\n\n");

        m = (pointy2 - pointy1) / (pointx2 - pointx1);
        a = m;
        b = -1;
        c = -m * pointx1 + pointy1;
        d = sqrt((a * opointx + b * opointy + c) * (a * opointx + b * opointy + c) / (a * a) + (b * b));

        if (d < r)
            printf("교점은 2개입니다");
        else if (d = r)
            printf("교점은 1개입니다");
        else if (d > r)
            printf("교점은 없습니다");

    }*/

    // 원과 라인의 교점을 구하는 함수

    // 교점이 있으면 좌표를 xy배열에 저장하고 교점의 수를 리턴하는 함수
    /*
    int main(void) {
        // x,y : 원의 중심좌표, r: 반경
        float x, y, r;

        // (a,b),(c,d) : 직선을 이루는 두점의 좌표
        // m,n : 두점을 지나는 직선의 기울기와 절편
        float a, b, c, d;
        int n;

        // 교점의 좌표 두개
        float xy[ 2][2];

        while (1) {
            printf("원의 중심좌표 입력\n");
            printf("x 좌표 >> ");
            scanf("%f", & x );

            printf("y 좌표 >> ");
            scanf("%f", & y );

            printf("반경 >> ");
            scanf("%f", & r );

            printf("\n직선의 두점 입력\n");
            printf("첫번째 점의 x 좌표 >> ");
            scanf("%f", & a );

            printf("첫번째 점의 y 좌표 >> ");
            scanf("%f", & b );

            printf("\n두번째 점의 x 좌표 >> ");
            scanf("%f", & c );

            printf("두번째 점의 y 좌표 >> ");
            scanf("%f", & d );

            // 함수 요청
            n = intersection(x, y, r, a, b, c, d, xy);

            if (n == 0)
                printf("\n직선과 원은 맞나지 않습니다.\n");
            else if (n == 1)
                printf("?n太굅?원은 한 점(%.2f, %.2f)에서 맞납니다.\n", xy[0][0], xy[0][1]);
            else if (n == 2)
                printf("\n직선과 원은 두 점(%.2f, %.2f)와 (%.2f, %.2f)에서 맞납니다.\n", xy[0][0], xy[0][1], xy[1][0], xy[1][1]);

            // 버퍼비우기
            getchar();
            printf("\n종료하시려면 (Q/q)키를 \n계속해서 테스트하시려면 그밖의 아무 키나 누르십시오 >> ");
            if ((c = getchar()) == 'Q' || c == 'q') {
                printf("프로그램을 종료합니다.\n");
                break;
            }
            printf("\n");
        } // end while(1)
        return 0;
    }*/

    /*
    원의 중심을 x, y, 반경을 r
    두점(a,b),(c,d)를 지나는 직선을
    Y = mX + n라고 하면
    m = (d-b)/(c-a)
    n = (bc-ad)/(c-a)

    1) m에서 분모 c!=a이면
      (X - x)^2 + (Y - y)^2 = r^2 에 Y = mX + n를 대입
      X^2 - 2Xx + x^2 + (mX + (n-y))^2 - r^2 = 0

      X^2 - 2Xx + x^2 + (m^2X^2 + 2mX(n-y) + n^2-2ny+y^2) - r^2 = 0
      X^2( 1 + m^2) + 2X(m(n-y)-x) + (x^2 + n^2 - 2ny + y^2 - r^2) = 0
      X^2( m^2 + 1 ) + 2X( mn-my-x) + (x^2 + y^2 - r^2 + n^2 - 2ny) = 0

      위는 이차 방정식이므로
      AX^2 + 2BX + C = 0 로 하면
      A = m^2 + 1, B = 2(mn-my-x), C = (x^2 + y^2 - r^2 + n^2 - 2ny)
      B = 2B'로 두면 B' = mn-my-x
      판별식 D = B'^2 - AC

    2) m에서 분모가 0( c==a )이면 수직선이므로
       X == a라는 수직선의 위치에 따라서 달라진다.
    */

    //    public static int intersection(float x, float y, float r, float a, float b, float c, float d, float xy[][2]) {
    public static int intersection(double x, double y, double r, double a, double b, double c, double d) {
        double m, n;
        double[][] xy = new double[2][2];

        // A,B1,C 원과 직선으로부터 얻어지는 2차방정식의 계수들
        // D: 판별식
        // X,Y: 교점의 좌표
        double A, B1, C, D;
        double X, Y;

        // A,B1,C,D게산
        if (c != a) {
            // m, n계산
            m = (d - b) / (c - a);
            n = (b * c - a * d) / (c - a);

            A = m * m + 1;
            B1 = (m * n - m * y - x);
            C = (x * x + y * y - r * r + n * n - 2 * n * y);
            D = B1 * B1 - A * C;

            if (D < 0)
                return 0;
            else if (D == 0) {
                X = -B1 / A;
                Y = m * X + n;
                xy[0][0] = X;
                xy[0][1] = Y;
                return 1;
            } else {
                X = -(B1 + sqrt(D)) / A;
                Y = m * X + n;
                xy[0][0] = X;
                xy[0][1] = Y;

                X = -(B1 - sqrt(D)) / A;
                Y = m * X + n;
                xy[1][0] = X;
                xy[1][1] = Y;
                return 2;
            }
        } else {
            // a == c 인 경우는 수직선이므로
            // 근을 가지려면 a >= (x-r) && a <=(x+r) )
            // (a-x)*(a-x)
            // 1. 근이 없는 경우
            // a < (x-r) || a > (x+r)

            // 근이 없음
            if (a < (x - r) || a > (x + r))
                return 0;
                // 하나의 중근
            else if (a == (x - r) || a == (x + r)) {
                X = a;
                Y = y;
                xy[0][0] = X;
                xy[0][1] = Y;

                return 1;
            }
            // 두개의 근
            else {
                // x = a를 대입하여 Y에 대하여 풀면
                X = a;
                Y = y + sqrt(r * r - (a - x) * (a - x));
                xy[0][0] = X;
                xy[0][1] = Y;

                Y = y - sqrt(r * r - (a - x) * (a - x));
                xy[1][0] = X;
                xy[1][1] = Y;

                return 2;
            }
        }
    }
}

