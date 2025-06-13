-- Tabela de Criadores de Conteúdo
CREATE TABLE `criador` (
                           `id_criador` bigint NOT NULL AUTO_INCREMENT,
                           `cpf` varchar(255) DEFAULT NULL,
                           `email` varchar(255) DEFAULT NULL,
                           `formacao` varchar(255) DEFAULT NULL,
                           `foto_url` varchar(255) DEFAULT NULL,
                           `funcao` tinyint DEFAULT NULL,
                           `nome` varchar(255) DEFAULT NULL,
                           `senha` varchar(255) DEFAULT NULL,
                           `status_solicitacao` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id_criador`),
                           UNIQUE KEY `UKhe4lv586xmit3fo7xtcmvc3f6` (`cpf`),
                           UNIQUE KEY `UK1lsybt718a3cefamxvc4rhon2` (`email`),
                           CONSTRAINT `criador_chk_1` CHECK ((`funcao` between 0 and 2))
) ENGINE=InnoDB AUTO_INCREMENT=3;

-- Tabela de Usuários
CREATE TABLE `usuario` (
                           `id_usuario` bigint NOT NULL AUTO_INCREMENT,
                           `cadastro_completo` tinyint(1) NOT NULL DEFAULT '0',
                           `descricao` varchar(255) DEFAULT NULL,
                           `email` varchar(255) DEFAULT NULL,
                           `foto_url` varchar(255) DEFAULT NULL,
                           `funcao` tinyint DEFAULT NULL,
                           `interesses` varchar(255) DEFAULT NULL,
                           `nome` varchar(255) DEFAULT NULL,
                           `pergunta_resposta` varchar(100) DEFAULT NULL,
                           `senha` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id_usuario`),
                           UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`),
                           CONSTRAINT `usuario_chk_1` CHECK ((`funcao` between 0 and 2))
) ENGINE=InnoDB AUTO_INCREMENT=4;

-- Tabela de Vídeos
CREATE TABLE `video` (
                         `id_video` bigint NOT NULL AUTO_INCREMENT,
                         `categoria` varchar(255) DEFAULT NULL,
                         `data_publicacao` date DEFAULT NULL,
                         `descricao` varchar(255) DEFAULT NULL,
                         `video_key` varchar(255) DEFAULT NULL,
                         `palavra_chave` varchar(255) DEFAULT NULL,
                         `thumbnail` varchar(255) DEFAULT NULL,
                         `titulo` varchar(255) DEFAULT NULL,
                         `visualizacoes` bigint DEFAULT NULL,
                         `id_criador` bigint DEFAULT NULL,
                         PRIMARY KEY (`id_video`),
                         KEY `FK98vnlylcm77xb1j9x046fd1vk` (`id_criador`),
                         CONSTRAINT `FK98vnlylcm77xb1j9x046fd1vk` FOREIGN KEY (`id_criador`) REFERENCES `criador` (`id_criador`)
) ENGINE=InnoDB AUTO_INCREMENT=3;

-- Tabela de Assinaturas
CREATE TABLE `assinatura` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `data_fim` date DEFAULT NULL,
                              `data_inicio` date DEFAULT NULL,
                              `plano` tinyint DEFAULT NULL,
                              `preco` double DEFAULT NULL,
                              `id_usuario` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FKftf8mn05e7cffq5w78df9bawv` (`id_usuario`),
                              CONSTRAINT `FKftf8mn05e7cffq5w78df9bawv` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
                              CONSTRAINT `assinatura_chk_1` CHECK ((`plano` between 0 and 2))
) ENGINE=InnoDB AUTO_INCREMENT=3;

-- Tabela de Avaliações
CREATE TABLE `avaliacao` (
                             `id_avaliacao` bigint NOT NULL AUTO_INCREMENT,
                             `comentario` varchar(255) DEFAULT NULL,
                             `nota` int DEFAULT NULL,
                             `id_usuario` bigint DEFAULT NULL,
                             `id_video` bigint DEFAULT NULL,
                             PRIMARY KEY (`id_avaliacao`),
                             UNIQUE KEY `UKs997nbbsyhlx3dho0yxna0m1c` (`id_usuario`,`id_video`),
                             KEY `FKaee27q5lt6qmp60qwtxqh9har` (`id_video`),
                             CONSTRAINT `FKaee27q5lt6qmp60qwtxqh9har` FOREIGN KEY (`id_video`) REFERENCES `video` (`id_video`),
                             CONSTRAINT `FKik01lj37b4mcswtkppjc9yket` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3;

-- Tabela de Progresso do Usuário no Vídeo
CREATE TABLE `usuario_video_progresso` (
                                           `data_inicio` date DEFAULT NULL,
                                           `tempo_assistido` decimal(21,0) DEFAULT NULL,
                                           `id_avaliacao` bigint NOT NULL,
                                           `id_usuario` bigint NOT NULL,
                                           `id_video` bigint NOT NULL,
                                           PRIMARY KEY (`id_avaliacao`,`id_usuario`,`id_video`),
                                           KEY `FK588vmcis3y9xyte46rqiypwkq` (`id_usuario`),
                                           KEY `FK96rtew0q3i5s89yydhirj6ifw` (`id_video`),
                                           CONSTRAINT `FK588vmcis3y9xyte46rqiypwkq` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
                                           CONSTRAINT `FK96rtew0q3i5s89yydhirj6ifw` FOREIGN KEY (`id_video`) REFERENCES `video` (`id_video`),
                                           CONSTRAINT `FKabcjbmqmqr3ff0h37nq4ogi1w` FOREIGN KEY (`id_avaliacao`) REFERENCES `avaliacao` (`id_avaliacao`)
) ENGINE=InnoDB;

-- Tabela de Playlists
CREATE TABLE `playlist` (
                            `id_playlist` bigint NOT NULL AUTO_INCREMENT,
                            `nome` varchar(255) DEFAULT NULL,
                            `visibilidade` enum('NAO_LISTADA','PRIVADA','PUBLICA') DEFAULT NULL,
                            `id_criador` bigint DEFAULT NULL,
                            `id_usuario` bigint NOT NULL,
                            PRIMARY KEY (`id_playlist`),
                            KEY `FKr9w8aa84vug17qhqelykm4gji` (`id_criador`),
                            KEY `FK661qwbrq7rr4xyj61s8ra86oe` (`id_usuario`),
                            CONSTRAINT `FK661qwbrq7rr4xyj61s8ra86oe` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
                            CONSTRAINT `FKr9w8aa84vug17qhqelykm4gji` FOREIGN KEY (`id_criador`) REFERENCES `criador` (`id_criador`)
) ENGINE=InnoDB AUTO_INCREMENT=3;

-- Tabela de Relação Playlist-Vídeo
CREATE TABLE `playlist_video` (
                                  `data_adicao` date DEFAULT NULL,
                                  `posicao_video` int DEFAULT NULL,
                                  `id_playlist` bigint NOT NULL,
                                  `id_video` bigint NOT NULL,
                                  PRIMARY KEY (`id_playlist`,`id_video`),
                                  KEY `FK2b7trtyttkur93v401dlrmb2s` (`id_video`),
                                  CONSTRAINT `FK2b7trtyttkur93v401dlrmb2s` FOREIGN KEY (`id_video`) REFERENCES `video` (`id_video`),
                                  CONSTRAINT `FK5n8gk05p9hsf1odajvp3ubpy` FOREIGN KEY (`id_playlist`) REFERENCES `playlist` (`id_playlist`)
) ENGINE=InnoDB;

