package tcc;

import java.util.Scanner;

public class LoginSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        if (!email.contains("@")) {
            System.out.println("Email inválido. O email deve conter o caractere '@'.");
            return;
        }

        System.out.print("Digite sua senha (mínimo 8 caracteres, com números e caracteres especiais): ");
        String senha = scanner.nextLine();

        String mensagemErro = validarSenha(senha);
        if (!mensagemErro.isEmpty()) {
            System.out.println("Senha inválida: " + mensagemErro);
            return;
        }

        System.out.println("\nLogin realizado com sucesso!");
        System.out.println("Email: " + email);
        System.out.println("Senha: " + senha);

        scanner.close();
    }

    public static String validarSenha(String senha) {
        StringBuilder mensagemErro = new StringBuilder();

        if (senha.length() < 8) {
            mensagemErro.append("A senha deve ter no mínimo 8 caracteres. ");
        }

        boolean temNumero = senha.chars().anyMatch(Character::isDigit);
        if (!temNumero) {
            mensagemErro.append("A senha deve conter pelo menos um número. ");
        }

        boolean temCaractereEspecial = senha.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        if (!temCaractereEspecial) {
            mensagemErro.append("A senha deve conter pelo menos um caractere especial. ");
        }

        return mensagemErro.toString().trim();
    }
}
