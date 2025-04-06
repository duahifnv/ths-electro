#[derive(serde::Serialize, serde::Deserialize)]
pub struct Unit {
    pub day: u8,
    pub hours: Vec<u8>,
}
#[derive(serde::Serialize, serde::Deserialize)]
pub struct Dto {
    pub year: u32,
    pub month: u8,
    pub units: Vec<Unit>,
}