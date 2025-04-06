#[derive(serde::Deserialize)]
pub struct CreateDto {
    pub name: String,
}
#[derive(serde::Deserialize)]
pub struct UpdateDto {
    pub name: String,
}