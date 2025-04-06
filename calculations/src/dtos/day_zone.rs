#[derive(serde::Serialize, serde::Deserialize)]
pub struct Unit {
    pub hour: u8,
    pub zone_type: u8,
}
#[derive(serde::Serialize, serde::Deserialize)]
pub struct Dto {
    pub month: u8,
    pub units: Vec<Unit>,
}