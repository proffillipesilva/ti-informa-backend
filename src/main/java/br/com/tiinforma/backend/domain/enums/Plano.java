package br.com.tiinforma.backend.domain.enums;

public enum Plano {
    GRATUITO(0, "GRATUITO"),
    PREMIUM(1, "PREMIUM"),
    FAMILIA(2, "FAMILIA");

    private Integer codigo;
    private String descricao;

    Plano(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
    public String getDescricao() {
        return descricao;
    }

    public static Plano toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (Plano plano : Plano.values()){
            if (codigo.equals(plano.getCodigo())) {
                return plano;
            }
        }

        throw new IllegalArgumentException("Plano invalido codigo: " + codigo);
    }
}
