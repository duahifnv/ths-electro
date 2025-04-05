import * as XLSX from "xlsx";

const flattenObject = (obj, prefix = '') => {
    let result = {};

    for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
            const newKey = prefix ? `${prefix}.${key}` : key;

            if (typeof obj[key] === 'object' && obj[key] !== null && !Array.isArray(obj[key])) {
                const flattened = flattenObject(obj[key], newKey);
                result = { ...result, ...flattened };
            } else {
                result[newKey] = obj[key];
            }
        }
    }

    return result;
};

const useExcelExport = () => {
    const exportToExcel = (jsonData, fileName = "exported_data") => {
        if (!jsonData || jsonData.length === 0) {
            console.error("No data to export");
            return;
        }

        const flattenedData = jsonData.map(item => flattenObject(item));

        const ws = XLSX.utils.json_to_sheet(flattenedData);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
        XLSX.writeFile(wb, `${fileName}.xlsx`);
    };

    return { exportToExcel };
};

export default useExcelExport;