const EvtTitleComponent = ({ title }) => {
  return (
    <div className="w-full mt-4 mb-8 px-4 ml-[350px]">
      <h1 className="text-3xl font-bold mb-2">{title}</h1>
      <hr className="w-3/5 border-t-2 border-gray-300" />
    </div>
  );
};
export default EvtTitleComponent;