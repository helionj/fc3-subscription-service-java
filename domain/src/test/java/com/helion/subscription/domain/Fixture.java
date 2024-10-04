package com.helion.subscription.domain;



import net.datafaker.Faker;


public final class Fixture {

    private final static Faker FAKER = new Faker();

    public static String name(){
        return FAKER.name().fullName();
    }

    public static int year(){
        return FAKER.random().nextInt(2020,2030);
    }

    public static boolean bool(){
        return FAKER.bool().bool();
    }
    public static double duration(){
        return FAKER.options().option(120.0, 45.0, 25.5, 15.5, 90.0);
    }

    public static String title(){
        return FAKER.options()
                .option("System Design no Mercado Livre na prática",
                        "Não cometa esses errors ao trabalhar com microserviços",
                        "Testes de Mutação. Voce não testa o seu software corretamente");
    }

    public static String checksum() {
        return "03fe62de";
    }


        public static String description(){
            return FAKER.options()
                    .option("""
            Lets design the high-level architecture of youtube - similar to 
            how we'd tackle this in a system design interview.
            """,
            """
            Nesse vídeo você entenderá o que aconteceu na Amazon Prime Video que os fizeram sair dos microsserviços.
            Link do artigo:
            https://www.primevideotech.com/video-...
            """);
        }


}


