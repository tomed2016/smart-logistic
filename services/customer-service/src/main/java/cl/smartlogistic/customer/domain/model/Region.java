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
}
