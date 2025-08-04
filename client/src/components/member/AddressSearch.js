const AddressSearch = ({ onAddressSelected }) => {
    const openPostcode = () => {
        new window.daum.Postcode({
            oncomplete: function (data) {
                const address = data.address; // 사용자가 선택한 도로명 주소
                onAddressSelected(address);
            }
        }).open();
    };

    return (
        <div>
            <button
                onClick={openPostcode}
                className="float-left px-2 py-2 bg-gray-200 rounded-md hover:bg-gray-300 active:bg-gray-400 text-sm font-medium border border-gray-400"
            >
                주소 찾기
            </button>
        </div>
    );
};
export default AddressSearch;