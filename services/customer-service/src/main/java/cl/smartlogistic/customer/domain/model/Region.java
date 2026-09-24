package cl.smartlogistic.customer.domain.model;

/**
 * Las 16 regiones oficiales de Chile (division politico-administrativa vigente desde
 * la creacion de la Region de Ñuble en 2018). Modelada como enum porque es un
 * catalogo cerrado y estable, a diferencia de las comunas (mas numerosas y con mayor
 * probabilidad de reorganizacion administrativa), que se gestionan via
 * {@link Comuna} y su catalogo.
 */
public enum Region {
    ARICA_Y_PARINACOTA("Arica y Parinacota"),
    TARAPACA("Tarapacá"),
    ANTOFAGASTA("Antofagasta"),
    ATACAMA("Atacama"),
    COQUIMBO("Coquimbo"),
    VALPARAISO("Valparaíso"),
    METROPOLITANA_DE_SANTIAGO("Metropolitana de Santiago"),
    LIBERTADOR_GENERAL_BERNARDO_OHIGGINS("Libertador General Bernardo O'Higgins"),
    MAULE("Maule"),
    NUBLE("Ñuble"),
    BIOBIO("Biobío"),
    LA_ARAUCANIA("La Araucanía"),
    LOS_RIOS("Los Ríos"),
    LOS_LAGOS("Los Lagos"),
    AYSEN_DEL_GENERAL_CARLOS_IBANEZ_DEL_CAMPO("Aysén del General Carlos Ibáñez del Campo"),
    MAGALLANES_Y_ANTARTICA_CHILENA("Magallanes y de la Antártica Chilena");

    private final String nombreOficial;

    Region(String nombreOficial) {
        this.nombreOficial = nombreOficial;
    }

    public String nombreOficial() {
        return nombreOficial;
    }

    /**
     * Resuelve el {@link Region} correspondiente a un codigo oficial de region INE
     * (1-16). Puente explicito hacia el catalogo geografico compartido
     * (geo-catalog-service), cuyo modelo de dominio identifica las regiones por este
     * codigo numerico en vez de por este enum cerrado. Ver
     * docs/architecture/05-integracion-clientes-catalogo-geografico.md para el
     * razonamiento de por que ambas representaciones coexisten en esta iteracion.
     *
     * @throws IllegalArgumentException si el codigo no corresponde a ninguna de las
     *                                   16 regiones oficiales.
     */
    public static Region porCodigoIne(int codigoIne) {
        return switch (codigoIne) {
            case 1 -> TARAPACA;
            case 2 -> ANTOFAGASTA;
            case 3 -> ATACAMA;
            case 4 -> COQUIMBO;
            case 5 -> VALPARAISO;
            case 6 -> LIBERTADOR_GENERAL_BERNARDO_OHIGGINS;
            case 7 -> MAULE;
            case 8 -> BIOBIO;
            case 9 -> LA_ARAUCANIA;
            case 10 -> LOS_LAGOS;
            case 11 -> AYSEN_DEL_GENERAL_CARLOS_IBANEZ_DEL_CAMPO;
            case 12 -> MAGALLANES_Y_ANTARTICA_CHILENA;
            case 13 -> METROPOLITANA_DE_SANTIAGO;
            case 14 -> LOS_RIOS;
            case 15 -> ARICA_Y_PARINACOTA;
            case 16 -> NUBLE;
            default -> throw new IllegalArgumentException(
                    "Codigo de region INE invalido, se esperaba un valor entre 1 y 16: " + codigoIne);
        };
    }
}
