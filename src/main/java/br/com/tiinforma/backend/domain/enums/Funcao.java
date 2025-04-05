package br.com.tiinforma.backend.domain.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Funcao {
    USUARIO(0, "USUARIO"),
    CRIADOR(1, "CRIADOR");

    private Integer codigo;
    private String descricao;

    public static Funcao toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (Funcao funcao : Funcao.values()){
            if (codigo.equals(funcao.getCodigo())) {
                return funcao;
            }
        }

        throw new IllegalArgumentException("Função invalida codigo: " + codigo);
    }
}
