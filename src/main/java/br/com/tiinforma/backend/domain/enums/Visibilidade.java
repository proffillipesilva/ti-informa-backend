package br.com.tiinforma.backend.domain.enums;

public enum Visibilidade {
    PUBLICA(0,"PUBLICA"),
    NAO_LISTADA(1, "NÃO LISTADA"),
    PRIVADA(2, "PRIVADA");

    private Integer codigo;
    private String descricao;

    Visibilidade(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
    public String getDescricao() {
        return descricao;
    }

    public static Visibilidade toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (Visibilidade visibilidade : Visibilidade.values()){
            if (codigo.equals(visibilidade.getCodigo())) {
                return visibilidade;
            }
        }

        throw new IllegalArgumentException("Visibilidade invalida codigo: " + codigo);
    }
}
