package tcc;

import java.util.Scanner;

public class MetodosPagamento {

    public static void pagarComCartao(double valor) {
        System.out.println("Pagamento de R$" + valor + " realizado com cartão.");
    }

    public static void pagarComBoleto(double valor) {
        System.out.println("Boleto gerado no valor de R$" + valor + ".");
    }

    public static void pagarComPix(double valor) {
        System.out.println("Pagamento de R$" + valor + " realizado com PIX.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o valor da compra: ");
        double valor = scanner.nextDouble();

        System.out.println("Escolha o método de pagamento:");
        System.out.println("1 - Cartão");
        System.out.println("2 - Boleto");
        System.out.println("3 - PIX");
        int opcao = scanner.nextInt();

        switch (opcao) {
            case 1:
                pagarComCartao(valor);
                break;
            case 2:
                pagarComBoleto(valor);
                break;
            case 3:
                pagarComPix(valor);
                break;
            default:
                System.out.println("Opção inválida.");
        }

        scanner.close();
    }
}
