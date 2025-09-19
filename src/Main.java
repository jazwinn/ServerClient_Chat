//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

void main() throws IOException {
    System.out.print("Insert Server Port: ");
    Scanner sc = new Scanner(System.in);
    int port = sc.nextInt();

    Server server = new Server(port);

    server.listen();

}
