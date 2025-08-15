set -e

PEM="/c/Users/nlcc3/Downloads/roarbitskeyp.pem"

HOST="ec2-user@15.164.169.229"

JAR=$(ls -1t build/libs/*.jar 2>/dev/null | head -n 1 || echo "build/libs/roarbits-0.0.1-SNAPSHOT.jar")

./gradlew clean bootJar -x test

scp -i "$PEM" "$JAR" "$HOST:/home/ec2-user/roarbits.jar"

ssh -i "$PEM" $HOST 'sudo systemctl restart roarbits; sudo journalctl -u roarbits -n 80 --no-pager'

