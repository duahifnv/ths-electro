#[derive(serde::Serialize, serde::Deserialize)]
pub struct Dto {
    pub year: u32,
    pub month: u8,
    pub days: Vec<u8>
}