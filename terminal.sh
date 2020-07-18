rm -rf "terminal.started"
rm -rf "terminal.stopped"
echo "Starting terminal" >>"terminal.started"
java -jar gode-terminal.jar --server.port=$1 --dir=$2
rm -rf "terminal.started"
echo "Stopped terminal" >>"terminal.stopped"
