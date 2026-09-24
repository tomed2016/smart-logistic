package cl.smartlogistic.shared.calendar;

/**
 * Origen/categoria de un {@link Feriado} chileno, util para trazabilidad y para
 * distinguir feriados calculables de forma perpetua de aquellos declarados por leyes
 * puntuales (que no pueden derivarse algoritmicamente).
 */
public enum TipoFeriado {
    /** Fecha fija en el calendario gregoriano (ej. 1 de enero). */
    FIJO,
    /** Calculado en base al Domingo de Pascua (ej. Viernes Santo). */
    MOVIL_PASCUA,
    /**
     * Feriado de fecha fija que la Ley 19.668 traslada al lunes mas cercano
     * (San Pedro y San Pablo, Encuentro de Dos Mundos).
     */
    MOVIL_LEY_19668,
    /**
     * Feriado adicional declarado por una ley especifica para un anio puntual
     * (ej. feriados "puente" de Fiestas Patrias segun Ley 20.983, u otras leyes
     * ad-hoc). No se puede derivar algoritmicamente para anios futuros: debe
     * incorporarse explicitamente en {@link CalendarioChileno} cuando la ley exista.
     */
    AD_HOC
}
