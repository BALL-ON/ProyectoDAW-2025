export interface MensajeDTO {
    idMensaje?: number;
    nombre: string,
    email: string,
    telefono: string,
    asunto:string,
    mensaje: string,
    fecha_envio: string,
    leido: boolean
}
