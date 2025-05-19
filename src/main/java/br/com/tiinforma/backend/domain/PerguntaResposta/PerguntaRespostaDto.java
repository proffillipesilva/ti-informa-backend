package br.com.tiinforma.backend.domain.PerguntaResposta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerguntaRespostaDto {
    private String pergunta;
    private String resposta;
}