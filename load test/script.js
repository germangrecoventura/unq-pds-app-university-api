import http from "k6/http";
import { check, sleep } from "k6";
// In case of using Windows and Docker, put the IP v4 declared in the ipconfig command
//const url = "http://ip:8080/login";

export const options = {
  stages: [
    { duration: "30s", target: 20 },
    { duration: "1m30s", target: 10 },
    { duration: "20s", target: 0 },
  ],
};

export default function () {
  let data = { email: "admin@gmail.com", password: "funciona" };

  // Using a JSON string as body
  let res = http.post(url, JSON.stringify(data), {
    headers: { "Content-Type": "application/json" },
  });
  check(res, { "status was 200": (r) => r.status == 200 });
  sleep(1);
}
